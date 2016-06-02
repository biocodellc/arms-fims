<?php

/**
 * @file
 * Contains \Drupal\arms\Form\SnippetsSettingsForm.
 */

namespace Drupal\arms\Form;

use Drupal\Core\Form\ConfigFormBase;
use Drupal\Core\Form\FormStateInterface;

class ArmsSearchForm extends ConfigFormBase {
  /**
   * {@inheritdoc}
   */
  public function getFormId() {
    return 'arms_search';
  }

  /**
   * {@inheritdoc}
   */
  public function buildForm(array $form, FormStateInterface $form_state, $options = NULL) {
    $form['expeditions'] = [
      '#type' => 'select',
      '#title' => $this->t('Choose Project'),
      '#empty_option' => $this->t('Select a project'),
      '#default_value' => '',
      '#options' => $options,
      '#ajax' => [
        'callback' => '::getDeployments',
        'wrapper' => 'deployment-wrapper',
      ],
    ];

    // Disable caching on this form.
    $form_state->setCached(FALSE);

    $form['deployment_wrapper'] = [
      '#type' => 'container',
      '#attributes' => ['id' => 'deployment-wrapper'],
    ];

    return parent::buildForm($form, $form_state);
  }

  public function getDeployments(array &$form, FormStateInterface $form_state) {
    $expedition_id = $form_state->getValue('expeditions');
    if ($expedition_id != "") {
      $arms_config = \Drupal::config('arms.settings');
      $rest_root = $arms_config->get("arms_rest_uri");

      $client = \Drupal::service('http_client');
      try {
        $result = $client->get(
          $rest_root . 'arms/projects/' . $expedition_id,
          ['Accept' => 'application/json']
        );
        $expedition = json_decode($result->getBody());
      }
      catch (RequestException $e) {
        watchdog_exception('arms', $e);
        drupal_set_message('Error fetching project.', 'error');
      }
    }

    $options = [];

    foreach ($expedition->{'deployments'} as $deployment_id) {
      array_push($options, $deployment_id);
    }

    $form['deployment_wrapper']['deployments'] = [
      '#type' => 'select',
      '#multiple' => TRUE,
      '#title' => $this->t('Choose Deployment(s)'),
      '#options' => $options,
    ];

    return $form['deployment_wrapper'];
  }

  /**
   * Gets the configuration names that will be editable.
   *
   * @return array
   *   An array of configuration object names that are editable if called in
   *   conjunction with the trait's config() method.
   */
  protected function getEditableConfigNames() {
    // TODO: Implement getEditableConfigNames() method.
  }
}
