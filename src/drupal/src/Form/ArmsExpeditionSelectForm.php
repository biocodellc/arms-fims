<?php

/**
 * @file
 * Contains \Drupal\arms\Form\SnippetsSettingsForm.
 */

namespace Drupal\arms\Form;

use Drupal\Core\Form\ConfigFormBase;
use Drupal\Core\Form\FormStateInterface;

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
  public function buildForm(array $form, FormStateInterface $form_state) {
    $form['expeditions'] = array(
      '#type' => 'select',
      '#title' => $this->t('Choose Project'),
      '#empty_option' => $this->t('Select a project'),
      '#default_value' => '',
      '#ajax' => [
        'callback' => 'Drupal\arms\Controller\ArmsExpeditionController::expeditionDetail',
      ],
    );

    return parent::buildForm($form, $form_state);
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
