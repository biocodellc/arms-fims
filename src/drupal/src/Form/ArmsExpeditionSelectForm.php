<?php

/**
 * @file
 * Contains \Drupal\arms\Form\SnippetsSettingsForm.
 */

namespace Drupal\arms\Form;

use Drupal\Core\Form\ConfigFormBase;
use Drupal\Core\Form\FormStateInterface;
use Drupal\Core\Ajax\AjaxResponse;
use Drupal\Core\Ajax\HtmlCommand;

class ArmsExpeditionSelectForm extends ConfigFormBase {
  /**
   * {@inheritdoc}
   */
  public function getFormId() {
    return 'arms_expeditions';
  }

  /**
   * {@inheritdoc}
   */
  public function buildForm(array $form, FormStateInterface $form_state, $options = null) {
    $form['expeditions'] = array(
      '#type' => 'select',
      '#title' => $this->t('Choose Project'),
      '#empty_option' => $this->t('Select a project'),
      '#default_value' => '',
      '#options' => $options,
      '#ajax' => [
        'callback' => 'Drupal\arms\Form\ArmsExpeditionSelectForm::expeditionDetail',
      ],
    );

    return parent::buildForm($form, $form_state);
  }

  public function expeditionDetail($form, $form_state) {
    $expedition = [];
    $response = new AjaxResponse();
    if ($form['expeditions']['#value'] != "") {
      $arms_config = \Drupal::config('arms.settings');
      $rest_root = $arms_config->get("arms_rest_uri");

      $client = \Drupal::service('http_client');
      $result = $client->get(
        $rest_root . 'arms/projects/' . $form['expeditions']['#value'],
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

  /**
   * Gets the configuration names that will be editable.
   *
   * @return array
   *   An array of configuration object names that are editable if called in
   *   conjunction with the trait's config() method.
   */
  protected function getEditableConfigNames() {
    // TODO: Implement getEditableConfigNames() method.
    return [];
  }
}
