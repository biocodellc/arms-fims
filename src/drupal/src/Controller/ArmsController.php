<?php

/**
 * @file
 * Contains \Drupal\arms\Controller\ArmsController.
 */

namespace Drupal\arms\Controller;

use Drupal\Core\Controller\ControllerBase;

class ArmsController extends ControllerBase {
  private $client;
  private $restRoot;

  public function __construct() {
    $this->client = \Drupal::service('http_client');
    $arms_config = \Drupal::config('arms.settings');
    $this->restRoot = $arms_config->get("arms_rest_uri");
  }

  public function listProjects() {
    $result = $this->client->get($this->restRoot . 'arms/projects', ['Accept' => 'application/json']);
    $expeditions = json_decode($result->getBody());

    $options = [];
    foreach ($expeditions as $value) {
      $options[$value->{'expedition'}->{'expeditionId'}] = $this->t($value->{'expedition'}->{'expeditionTitle'});
    }
    $select_form = \Drupal::formBuilder()->getForm('Drupal\arms\Form\ArmsExpeditionSelectForm', $options);

    // Remove the submit button.
    unset($select_form['actions']['submit']);

    return array(
      '#theme' => 'arms_expeditions',
      '#form' => $select_form,
    );
  }



  public function deploymentDetail($project_id, $deployment_id) {
    $result = $this->client->get(
      $this->restRoot . 'deployments/' . $project_id . '/' . $deployment_id,
      ['Accept' => 'application/json']
    );

    $deployment = json_decode($result->getBody());

    if ($deployment == NULL) {
      drupal_set_message("Deployment " . $deployment_id . " doesn't exist for projectId: " . $project_id . ".", 'error');
    }

    return array(
      '#theme' => 'arms_deployment_detail',
      '#deployment' => $deployment,
      '#deploymentId' => $deployment_id,
      '#projectId' => $project_id,
    );

  }
}