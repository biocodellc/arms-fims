<?php

/**
 * @file
 * Contains \Drupal\arms\Form\SnippetsSettingsForm.
 */

namespace Drupal\arms\Form;

use Drupal\arms\Controller\ArmsController;
use Drupal\Core\Form\FormBase;
use Drupal\Core\Form\FormStateInterface;
use Drupal\Core\Ajax\AjaxResponse;
use Drupal\Core\Ajax\HtmlCommand;

class ArmsExpeditionSelectForm extends FormBase {
  /**
   * {@inheritdoc}
   */
  public function getFormId() {
    return 'arms_expeditions';
  }

  /**
   * {@inheritdoc}
   */
  public function buildForm(array $form, FormStateInterface $form_state) {
    $arms_controller = new ArmsController();
    $expeditions = $arms_controller->getExpeditions();
    $form['expeditions'] = [
      '#type' => 'select',
      '#title' => $this->t('Choose Project'),
      '#empty_option' => $this->t('Select a project'),
      '#default_value' => '',
      '#options' => $expeditions,
      '#ajax' => [
        'callback' => '::expeditionDetail',
      ],
    ];

    $form['expedition_detail'] = [
      '#type' => 'container',
      '#attributes' => [
        'id' => 'expedition-detail',
      ],
    ];

    return $form;
  }

  public function expeditionDetail($form, $form_state) {
    $expedition = [];
    $response = new AjaxResponse();
    if ($form['expeditions']['#value'] != "") {
      $arms_config = \Drupal::config('arms.settings');
      $rest_root = $arms_config->get("arms_rest_uri");

      $client = \Drupal::service('http_client');
      try {
        $result = $client->get(
          $rest_root . 'arms/projects/' . $form['expeditions']['#value'],
          ['Accept' => 'application/json']
        );
        $expedition = json_decode($result->getBody());
      }
      catch (RequestException $e) {
        watchdog_exception('arms', $e);
        drupal_set_message('Error fetching project.', 'error');
      }
    }


    $response->addCommand(new HtmlCommand(
      '#expedition-detail',
      [
        '#theme' => 'arms_expeditions_detail',
        '#expedition' => $expedition,
      ]
    ));

    return $response;
  }

  /**
   * Form submission handler.
   *
   * @param array $form
   *   An associative array containing the structure of the form.
   * @param \Drupal\Core\Form\FormStateInterface $form_state
   *   The current state of the form.
   */
  public function submitForm(array &$form, FormStateInterface $form_state) {
    // do nothing as this is ajax only, no submission
  }
}
