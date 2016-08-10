<?php

/**
 * @file
 * Contains \Drupal\arms\Controller\ArmsController.
 */

namespace Drupal\arms\Controller;

use Drupal\Core\Controller\ControllerBase;
use GuzzleHttp\Exception\RequestException;

class ArmsController extends ControllerBase {
  private $client;
  private $restRoot;

  public function __construct() {
    $this->client = \Drupal::service('http_client');
    $arms_config = \Drupal::config('arms.settings');
    $this->restRoot = $arms_config->get("arms_rest_uri");
  }

  public function getExpeditions() {
    $options = [];
    try {
      $result = $this->client->get($this->restRoot . 'arms/projects', ['Accept' => 'application/json']);
      $expeditions = json_decode($result->getBody());

      foreach ($expeditions as $value) {
        $options[$value->expeditionId] = $this->t($value->expedition->expeditionTitle);
      }
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      drupal_set_message('Error fetching projects.', 'error');
    }
    return $options;
  }


  public function deploymentDetail($project_id, $deployment_id) {
    $deployment = [];
    $expedition = [];
    try {
      $result = $this->client->get(
        $this->restRoot . 'deployments/' . $project_id . '/' . $deployment_id,
        ['Accept' => 'application/json']
      );
      $deployment = json_decode($result->getBody());

      if ($deployment == NULL) {
        drupal_set_message("Deployment " . $deployment_id . " doesn't exist for projectId: " . $project_id . ".", 'error');
      }
      else {
        try {
          $result = $this->client->get(
            $this->restRoot . 'arms/projects/' . $deployment->expeditionId,
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
        $this->restRoot . 'deployments/' . $identifier,
        ['Accept' => 'application/json']
      );
      $deployment = json_decode($result->getBody());

      if ($deployment == NULL) {
        drupal_set_message("Deployment " . $identifier . " doesn't exist.", 'error');
      }
      else {
        try {
          $result = $this->client->get(
            $this->restRoot . 'arms/projects/' . $deployment->expeditionId,
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
        $this->restRoot . 'arms/projects/' . $expedition_id,
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
        $this->restRoot . 'arms/projects/' . $identifier,
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
}