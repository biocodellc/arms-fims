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

  public function listProjects() {
    $options = $this->getExpeditions();

    $select_form = \Drupal::formBuilder()
      ->getForm('Drupal\arms\Form\ArmsExpeditionSelectForm', $options);

    // Remove the submit button.
    unset($select_form['actions']['submit']);

    return [
      '#theme' => 'arms_expeditions',
      '#form' => $select_form,
    ];
  }
  
  private function getExpeditions() {
    $options = [];
    try {
      $result = $this->client->get($this->restRoot . 'arms/projects', ['Accept' => 'application/json']);
      $expeditions = json_decode($result->getBody());

      foreach ($expeditions as $value) {
        $options[$value->{'expedition'}->{'expeditionId'}] = $this->t($value->{'expedition'}->{'expeditionTitle'});
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
  
  public function search() {
    $options = $this->getExpeditions();

    $search_form = \Drupal::formBuilder()
      ->getForm('Drupal\arms\Form\ArmsSearchForm', $options);

    // Remove the submit button.
    // unset($search_form['actions']['submit']);

    return [
      '#theme' => 'arms_search',
      '#form' => $search_form,
    ];
  }
}