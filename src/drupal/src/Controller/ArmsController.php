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
        $options[$value->{'expeditionId'}] = $this->t($value->{'expedition'}->{'expeditionTitle'});
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
    try {
      $result = $this->client->get(
        $this->restRoot . 'deployments/' . $project_id . '/' . $deployment_id,
        ['Accept' => 'application/json']
      );
      $deployment = json_decode($result->getBody());

      if ($deployment == NULL) {
        drupal_set_message("Deployment " . $deployment_id . " doesn't exist for projectId: " . $project_id . ".", 'error');
      }
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      drupal_set_message('Error fetching deployment.', 'error');
    }

    return [
      '#theme' => 'arms_deployment_detail',
      '#deployment' => $deployment,
      '#deploymentId' => $deployment_id,
      '#projectId' => $project_id,
    ];

  }
}