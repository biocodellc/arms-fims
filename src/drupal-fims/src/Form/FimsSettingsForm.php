<?php

/**
 * @file
 * Contains \Drupal\fims\Form\SnippetsSettingsForm.
 */

namespace Drupal\fims\Form;

use Drupal\Core\Form\ConfigFormBase;
use Drupal\Core\Form\FormStateInterface;

class FimsSettingsForm extends ConfigFormBase {
  /**
   * {@inheritdoc}
   */
  public function getFormId() {
    return 'fims_admin_settings';
  }

  /**
   * {@inheritdoc}
   */
  public function buildForm(array $form, FormStateInterface $form_state) {
    $config = $this->config('fims.settings');

    $form['oauth2_client_id'] = array(
      '#type' => 'textfield',
      '#title' => $this->t('Fims Instance OAuth2 Client ID'),
      '#default_value' => $config->get('oauth2_client_id'),
    );

    $form['oauth2_client_secret'] = array(
      '#type' => 'textfield',
      '#title' => $this->t('Fims Instance OAuth2 Client Secret'),
      '#default_value' => $config->get('oauth2_client_secret'),
    );

    $form['fims_rest_uri'] = array(
      '#type' => 'textfield',
      '#title' => $this->t('REST root of FIMS instance'),
      '#default_value' => $config->get('fims_rest_uri'),
    );

    $form['fims_resolver_rest_uri'] = array(
      '#type' => 'textfield',
      '#title' => $this->t('Resolver REST root of FIMS instance'),
      '#default_value' => $config->get('fims_resolver_rest_uri'),
    );

    return parent::buildForm($form, $form_state);
  }

  /**
   * {@inheritdoc}
   */
  public function submitForm(array &$form, FormStateInterface $form_state) {
    $this->config('fims.settings')
      ->set('oauth2_client_id', $form_state->getValue('oauth2_client_id'))
      ->set('oauth2_client_secret', $form_state->getValue('oauth2_client_secret'))
      ->set('fims_rest_uri', $form_state->getValue('fims_rest_uri'))
      ->set('fims_resolver_rest_uri', $form_state->getValue('fims_resolver_rest_uri'))
      ->save();

    parent::submitForm($form, $form_state);
  }

  /**
   * {@inheritdoc}
   */
  protected function getEditableConfigNames() {
    return ['fims.settings'];
  }
}
