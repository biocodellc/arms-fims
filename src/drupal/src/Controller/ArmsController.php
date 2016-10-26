<?php

/**
 * @file
 * Contains \Drupal\arms\Controller\ArmsController.
 */

namespace Drupal\arms\Controller;

use Drupal\Component\Render\FormattableMarkup;
use Drupal\Core\Cache\CacheableResponse;
use Drupal\Core\Controller\ControllerBase;
use Drupal\file\Entity\File;
use GuzzleHttp\Exception\RequestException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ArmsController extends ControllerBase {
  private $client;
  private $rest_root;
  private $config;

  public function __construct() {
    $this->client = \Drupal::service('http_client');
    $this->config = \Drupal::config('arms.settings');
    $this->rest_root = $this->config->get("arms_rest_uri");
  }

  public function deploymentDetail($project_id, $deployment_id) {
    $deployment = [];
    $expedition = [];
    try {
      $result = $this->client->get(
        $this->rest_root . 'deployments/' . $project_id . '/' . $deployment_id,
        ['Accept' => 'application/json']
      );
      $deployment = json_decode($result->getBody());

      if ($deployment == NULL) {
        drupal_set_message("Deployment " . $deployment_id . " doesn't exist for projectId: " . $project_id . ".", 'error');
      }
      else {
        try {
          $result = $this->client->get(
            $this->rest_root . 'arms/projects/' . $deployment->expeditionId,
            ['Accept' => 'application/json']
          );
          $expedition = json_decode($result->getBody());
        }
        catch (RequestException $e) {
        }
      }
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      drupal_set_message('Error fetching deployment.', 'error');
    }

    return [
      '#theme' => 'arms_deployment_detail',
      '#deployment' => $deployment,
      '#expedition' => $expedition,
    ];

  }

  public function deploymentDetailByIdentifier($scheme, $naan, $shoulder, $suffix) {
    $deployment = [];
    $expedition = [];
    $identifier = $scheme . '/' . $naan . '/' . $shoulder . $suffix;
    try {
      $result = $this->client->get(
        $this->rest_root . 'deployments/' . $identifier,
        ['Accept' => 'application/json']
      );
      $deployment = json_decode($result->getBody());

      if ($deployment == NULL) {
        drupal_set_message("Deployment " . $identifier . " doesn't exist.", 'error');
      }
      else {
        try {
          $result = $this->client->get(
            $this->rest_root . 'arms/projects/' . $deployment->expeditionId,
            ['Accept' => 'application/json']
          );
          $expedition = json_decode($result->getBody());
        }
        catch (RequestException $e) {
        }
      }
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      drupal_set_message('Error fetching deployment.', 'error');
    }

    return [
      '#theme' => 'arms_deployment_detail',
      '#deployment' => $deployment,
      '#expedition' => $expedition,
    ];
  }

  public function deploymentDetailByIdentifierTitle($scheme, $naan, $shoulder, $suffix) {
    return 'Deployment: ' . $suffix;
  }

  public function expeditionDetailById($expedition_id) {
    $expedition = [];
    try {
      $result = $this->client->get(
        $this->rest_root . 'arms/projects/' . $expedition_id,
        ['Accept' => 'application/json']
      );
      $expedition = json_decode($result->getBody());

      if ($expedition == NULL) {
        drupal_set_message("Project with id " . $expedition_id . " doesn't exist.", 'error');
      }
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      drupal_set_message('Error fetching project.', 'error');
    }

    return [
      '#theme' => 'arms_expeditions_detail',
      '#expedition' => $expedition,
    ];
  }

  public function expeditionDetailByIdTitle($expedition_id) {
    return 'Project: ' . $expedition_id;
  }

  public function expeditionDetail($scheme, $naan, $shoulder_plus_suffix) {
    $expedition = [];
    $identifier = $scheme . "/" . $naan . "/" . $shoulder_plus_suffix;
    try {
      $result = $this->client->get(
        $this->rest_root . 'arms/projects/' . $identifier,
        ['Accept' => 'application/json']
      );
      $expedition = json_decode($result->getBody());

      if ($expedition == NULL) {
        drupal_set_message("Project with identifier " . $identifier . " doesn't exist.", 'error');
      }
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      drupal_set_message('Error fetching project.', 'error');
    }

    return [
      '#theme' => 'arms_expeditions_detail',
      '#expedition' => $expedition,
    ];
  }

  public function expeditionDetailTitle($scheme, $naan, $shoulder_plus_suffix) {
    return 'Project: ' . $shoulder_plus_suffix;
  }

  public function search() {
    return [
      '#theme' => 'arms_search',
    ];
  }

  public function fimsREST() {
    $config = $this->config('arms.settings');
    $uri = $config->get('arms_rest_uri');

    return new CacheableResponse("{\"uri\" : \"" . $uri . "\"}", 200, ['Content-Type' => 'application/json']);
  }

  public function queryExcel(Request $request) {
    $data = $this->client
      ->post(
        $this->rest_root . 'deployments/query/excel',
        [
          'accept' => 'application/octet-stream',
          'json' => json_decode($request->getContent(), TRUE),
        ]
      )
      ->getBody();

    $file = file_save_data($data);
    $file->setTemporary();
    $file->save();

    return new Response("{\"url\" : \"" . $this->getUrlGenerator()
        ->generateFromRoute("arms.search.excel.download", ['file_uri' => $file->getFilename()]) . "\"}", 200, ['Content-Type' => 'application/json']);
  }

  public function downloadFile($file_uri) {
    $data = file_get_contents("public://" . $file_uri);

    header("Content-Disposition: attachment; filename=\"" . urlencode('arms-fims-output.xlsx') . "\"");
    header("Content-length: " . strlen($data));

    echo $data;
  }

  public function queryMap(Request $request) {

    $data = $this->client
      ->post(
        $this->rest_root . 'deployments/query/tab',
        [
          'accept' => 'application/octet-stream',
          'json' => json_decode($request->getContent(), TRUE),
        ]
      )
      ->getBody();
    $file = file_save_data($data);
    $file->setTemporary();
    $file->save();

    $urltemplate = $this->config->get("berkeley_mapper_template_uri");
    $formatter = new FormattableMarkup(
      $urltemplate,
      [
        '@configfile' => $this->config->get('arms_berkeley_mapper_config_uri'),
        '@tabfile' => file_create_url($file->getFileUri()),
      ]
    );

    return new Response("{\"url\" : \"" . (string) $formatter . "\"}", 200, ['Content-Type' => 'application/json']);
  }
}