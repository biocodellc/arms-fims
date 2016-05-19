<?php

/**
 * @file
 * Contains \Drupal\arms\Controller\ArmsExpeditionController.
 */

namespace Drupal\arms\Controller;

use Drupal\Core\Ajax\AjaxResponse;
use Drupal\Core\Ajax\HtmlCommand;
use Drupal\Core\Controller\ControllerBase;

class ArmsExpeditionController extends ControllerBase {
  public $expeditions;

  public function listProjects() {
    $select_form = \Drupal::formBuilder()->getForm('Drupal\arms\Form\ArmsExpeditionSelectForm');
    // Remove the submit button.
    unset($select_form['actions']['submit']);

    $client = \Drupal::service('http_client');
    $result = $client->get('http://localhost:8080/arms/rest/arms/projects', ['Accept' => 'application/json']);
    $expeditions = json_decode($result->getBody());

    foreach ($expeditions as $value) {
      $select_form['expeditions']['#options'][$value->{'expedition'}->{'expeditionId'}] = $this->t($value->{'expedition'}->{'expeditionTitle'});
    }

    return array(
      '#theme' => 'arms_expeditions',
      '#form' => $select_form,
    );
  }

  public function expeditionDetail($form, $form_state) {
    $expedition = [];
    $response = new AjaxResponse();
    if ($form['expeditions']['#value'] != "") {

      $client = \Drupal::service('http_client');
      $result = $client->get(
        'http://localhost:8080/arms/rest/arms/projects/' . $form['expeditions']['#value'],
        ['Accept' => 'application/json']
      );
      $expedition = json_decode($result->getBody());
    }

    $response->addCommand(new HtmlCommand(
      '#expeditionDetail',
      array(
        '#theme' => 'arms_expeditions_detail',
        '#expedition' => $expedition,
      )
    ));

    return $response;
  }
}