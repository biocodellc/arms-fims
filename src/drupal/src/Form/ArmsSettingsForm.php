<?php

/**
 * @file
 * Contains \Drupal\fims\Form\SnippetsSettingsForm.
 */

namespace Drupal\arms\Form;

use Drupal\Core\Form\ConfigFormBase;
use Drupal\Core\Form\FormStateInterface;

class ArmsSettingsForm extends ConfigFormBase {
  /**
   * {@inheritdoc}
   */
  public function getFormId() {
    return 'arms_admin_settings';
  }

  /**
   * {@inheritdoc}
   */
  public function buildForm(array $form, FormStateInterface $form_state) {
    $config = $this->config('arms.settings');

    $form['arms_rest_uri'] = array(
      '#type' => 'textfield',
      '#title' => $this->t('REST root of arms-fims instance'),
      '#default_value' => $config->get('arms_rest_uri'),
    );
    
    $form['berkeley_mapper_template_uri'] = array(
      '#type' => 'textfield',
      '#title' => $this->t('berkeley mapper service template url'),
      '#description' => $this->t('should contain the variables @configfile and @tabfile'),
      '#default_value' => $config->get('berkeley_mapper_template_uri'),
    );
    
    $form['arms_berkeley_mapper_config_uri'] = array(
      '#type' => 'textfield',
      '#title' => $this->t('location of the arms berkeley mapper config file'),
      '#default_value' => $config->get('arms_berkeley_mapper_config_uri'),
    );
    
    return parent::buildForm($form, $form_state);
  }

  /**
   * {@inheritdoc}
   */
  public function submitForm(array &$form, FormStateInterface $form_state) {
    $this->config('arms.settings')
      ->set('arms_rest_uri', $form_state->getValue('arms_rest_uri'))
      ->set('berkeley_mapper_template_uri', $form_state->getValue('berkeley_mapper_template_uri'))
      ->set('arms_berkeley_mapper_config_uri', $form_state->getValue('arms_berkeley_mapper_config_uri'))
      ->save();

    parent::submitForm($form, $form_state);
  }

  /**
   * {@inheritdoc}
   */
  protected function getEditableConfigNames() {
    return ['arms.settings'];
  }
}
