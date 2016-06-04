<?php

/**
 * @file
 * Contains \Drupal\arms\Form\SnippetsSettingsForm.
 */

namespace Drupal\arms\Form;

use Drupal\arms\Controller\ArmsController;
use Drupal\Core\Ajax\AjaxResponse;
use Drupal\Core\Ajax\HtmlCommand;
use Drupal\Core\Ajax\InvokeCommand;
use Drupal\Core\Form\FormBase;
use Drupal\Core\Form\FormStateInterface;
use GuzzleHttp\Exception\RequestException;

class ArmsSearchForm extends FormBase {
  private static $filter_options_cache_id;
  private static $filter_methods = ['equals', 'contains', 'begins with'];

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
    $arms_controller = new ArmsController();
    $expeditions = $arms_controller->getExpeditions();
    $form['expeditions'] = [
      '#type' => 'select',
      '#title' => $this->t('Choose Project'),
      '#empty_option' => $this->t('Select a project'),
      '#default_value' => '',
      '#options' => $expeditions,
      '#ajax' => [
        'callback' => '::getDeployments',
        // 'wrapper' => 'deployment-wrapper',
      ],
    ];

    // Disable caching on this form.
    $form_state->setCached(FALSE);

    $form['deployments'] = [
      '#type' => 'container',
      '#attributes' => ['id' => 'deployments'],
    ];

    $form['filters'] = [
      '#type' => 'container',
      '#title' => $this->t('Filter'),
      '#attributes' => [
        'id' => "filters",
        'class' => ['hidden', 'form-group-sm'],
      ],
    ];

    $form['filters']['filter1'] = [
      '#type' => 'container',
      '#prefix' => "<span class=\"span-label\">Filter</span>",
      '#attributes' => [
        'class' => ['form-inline'],
      ],
    ];

    $form['filters']['filter1']['column'] = [
      '#type' => 'select',
      '#options' => $this->getFilterColumns(),
    ];

    $form['filters']['filter1']['method'] = [
      '#type' => 'select',
      '#options' => $this::$filter_methods,
    ];

    $form['filters']['filter1']['text'] = [
      '#type' => 'textfield',
    ];

    $form['filters']['filter1']['add'] = [
      '#type' => 'button',
      '#value' => '+',
      '#ajax' => [
        'callback' => '::addFilter',
        'wrapper' => 'filters',
        'method' => 'append',
      ],
    ];


    return $form;
  }

  public function getDeployments(array &$form, FormStateInterface $form_state) {
    $response = new AjaxResponse();
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

    $form['deployments'] = [
      '#type' => 'select',
      '#title' => $this->t('Choose Deployment(s)'),
      '#attributes' => [
        'multiple' => 'true',
      ],
      '#options' => $options,
      '#multiple' => TRUE,
    ];

    $response->addCommand(new HtmlCommand(
      '#deployments',
      $form['deployments']
    ));

    $response->addCommand(new InvokeCommand(
      '#filters',
      'removeClass',
      ['hidden']
    ));

    return $response;
  }

  public function addFilter(array $form, FormStateInterface $form_state) {
    $filter_id = uniqid('filter');

    $form['filters'][$filter_id] = [
      '#type' => 'container',
      '#field_prefix' => "<label for=edit-column class=control-label >And</label>",
      '#attributes' => [
        'class' => ['form-inline'],
      ],
    ];

    $form['filters'][$filter_id]['column'] = [
      '#type' => 'select',
      '#options' => $this->getFilterColumns(),
    ];

    $form['filters'][$filter_id]['method'] = [
      '#type' => 'select',
      '#options' => self::$filter_methods,
    ];

    $form['filters'][$filter_id]['text'] = [
      '#type' => 'textfield',
    ];

    return $form['filters'][$filter_id];
  }

  public function submitForm(array &$form, FormStateInterface $form_state) {
    parent::submitForm($form, $form_state); // TODO: Change the autogenerated stub
  }

  private function getFilterColumns() {
    $filter_options = [];
    if ($cache = \Drupal::cache()->get($this::$filter_options_cache_id)) {
#    deployment_id:
      $filter_options = $cache->data;
    }
    else {
      $arms_config = \Drupal::config('arms.settings');
      $rest_root = $arms_config->get("arms_rest_uri");

      $client = \Drupal::service('http_client');
      try {
        $result = $client->get(
          $rest_root . 'projects/' . $arms_config->get("arms_project_id") . '/filterOptions',
          ['Accept' => 'application/json']
        );
        $data = json_decode($result->getBody());

        foreach ($data as $option) {
          $filter_options[$option->uri] = $this->t($option->column);
        }
        \Drupal::cache()->set($this::$filter_options_cache_id, $filter_options);
      }
      catch (RequestException $e) {
        watchdog_exception('arms', $e);
        drupal_set_message('Error fetching filter options.', 'error');
      }
    }
    return $filter_options;
  }
}
