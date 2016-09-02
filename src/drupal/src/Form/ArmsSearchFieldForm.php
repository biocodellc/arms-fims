<?php
/**
 * @file
 * Contains \Drupal\arms\Form\SnippetsSettingsForm.
 */

namespace Drupal\arms\Form;


use Drupal\arms\Controller\ArmsFilterUtils;
use Drupal\Core\Ajax\AjaxResponse;
use Drupal\Core\Ajax\CloseModalDialogCommand;
use Drupal\Core\Form\FormBase;
use Drupal\Core\Form\FormStateInterface;

class ArmsSearchFieldForm extends FormBase {

  /**
   * {@inheritdoc}
   */
  public function getFormId() {
    return 'arms_search_fields';
  }

  /**
   * {@inheritdoc}
   */
  public function buildForm(array $form, FormStateInterface $form_state, $filters = NULL) {
    $filter_columns = ArmsFilterUtils::getFilterColumns();
    $display_columns = [];

    foreach($filter_columns as $column) {
      $display_columns[$column->column_internal] = $column->column;
    }

    $form['column'] = [
      '#type' => 'select',
      '#title' => $this->t('Choose Filter Column'),
      '#options' => $display_columns,
    ];

    $form['filters'] = [
      '#tree' => TRUE,
    ];

    foreach($filters as $existing_filter) {
      $form['filters'][$existing_filter] = [
        '#type' => 'hidden',
        '#value' => $existing_filter,
      ];
    }
    //
    // $form['buttons']['map'] = [
    //   '#type' => 'button',
    //   '#value' => 'map',
    //   '#name' => 'map',
    //   '#id' => 'query-map',
    //   '#ajax' => [
    //     'callback' => '::map',
    //   ],
    // ];
    $form['submit'] = [
      '#type' => 'submit',
      '#value' => 'Add',
      // '#name' => 'add',
    //   '#id' => 'add',
    //   '#ajax' => [
    //     'callback' => '::addFilter',
    //   ],
      '#attributes' => [
        'class' => ['use-ajax-submit'],
      ],
    ];

    return $form;
  }

  /**
   * {@inheritdoc}
   */
  public function submitForm(array &$form, FormStateInterface $form_state) {
    $filter_columns = [];

    foreach ($form_state->getValue('filters') as $column) {
      array_push($filter_columns, $column);
    }

    array_push($filter_columns, $form_state->getValue('column'));

    return \Drupal::formBuilder()->getForm('Drupal\arms\Form\ArmsSearchForm', $filter_columns);
  }

  public function addFilter(array &$form, FormStateInterface $form_state) {
    $response = new AjaxResponse();
    $response->addCommand(new CloseModalDialogCommand());
    // return $this->submitForm($form, $form_state);
    return $response;
  }
}