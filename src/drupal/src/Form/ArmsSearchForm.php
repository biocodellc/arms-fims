<?php

/**
 * @file
 * Contains \Drupal\arms\Form\SnippetsSettingsForm.
 */

namespace Drupal\arms\Form;

use Drupal\arms\Ajax\BootstrapSortableCommand;
use Drupal\arms\Controller\ArmsController;
use Drupal\arms\Controller\ArmsFilterUtils;
use Drupal\arms\Form\ArmsSearchFieldForm;
use Drupal\Component\Render\FormattableMarkup;
use Drupal\Core\Ajax\AjaxResponse;
use Drupal\Core\Ajax\HtmlCommand;
use Drupal\Core\Ajax\OpenModalDialogCommand;
use Drupal\Core\Ajax\PrependCommand;
use Drupal\arms\Ajax\RedirectCommand;
use Drupal\Core\Form\FormBase;
use Drupal\Core\Form\FormBuilderInterface;
use Drupal\Core\Form\FormState;
use Drupal\Core\Form\FormStateInterface;
use Drupal\Core\Url;
use GuzzleHttp\Exception\RequestException;

class ArmsSearchForm extends FormBase {
  private $OPERATOR_MAP = [
    "EQUALS" => "=",
    "LESS_THEN" => "<",
    "GREATER_THEN" => ">",
    "ENDS_WITH" => "Ends With",
    "STARTS_WITH" => "Starts With",
  ];

  /**
   * {@inheritdoc}
   */
  public function getFormId() {
    return 'arms_search';
  }

  /**
   * {@inheritdoc}
   */
  public function buildForm(array $form, FormStateInterface $form_state, $filter_columns = NULL) {

    $arms_controller = new ArmsController();
    $expeditions = $arms_controller->getExpeditions();
    $form['expeditions'] = [
      '#type' => 'select',
      '#title' => $this->t('Choose Project'),
      '#default_value' => '0',
      '#options' => ['0' => $this->t('All Projects')] + $expeditions,
      '#ajax' => [
        'callback' => '::showDeploymentsCallback',
        'wrapper' => 'toggle',
      ],
    ];

    $form['toggle'] = [
      '#type' => 'container',
      '#attributes' => ['id' => 'toggle'],
    ];

    if ($form_state->getValue("expeditions") != $form['expeditions']['#default_value'] &&
      $form_state->getValue("expeditions") != NULL
    ) {
      $form['toggle']['deployments'] = [
        '#type' => 'select',
        '#title' => $this->t('Choose Deployment(s)'),
        '#default_value' => '0',
        '#options' => $this->getDeployments($form_state->getValue("expeditions")),
        '#multiple' => TRUE,
      ];
    }

    $form['filters'] = [
      '#tree' => TRUE,
      '#type' => 'container',
      '#id' => 'filters',
      '#attributes' => [
        'class' => ['form-group-sm'],
      ],
    ];

    $filter_count = $form_state->get('filter_count');
    if (is_null($filter_count)) {
      $filter_count = 1;
      $form_state->set('filter_count', $filter_count);
    }

    // Add elements that don't already exist
    for ($delta = 0; $delta < $filter_count; $delta++) {
      if (!isset($form['filters'][$delta])) {
        if ($delta == 0) {
          $label = "Filter";
        }
        else {
          $label = "And";
        }

        $column = $this->getFilterColumnValue($delta, $form_state);
        $element = [
          '#type' => 'container',
          '#prefix' => "<span class=\"span-label\">" . $label . "</span>",
          '#attributes' => [
            'class' => ['form-inline'],
          ],
        ];

        $element_column = [
          '#type' => 'select',
          '#options' => $this->getFilterColumnsForDisplay(),
          '#default_value' => $column,
          '#ajax' => [
            'callback' => '::updateFilterOperatorsCallback',
            'wrapper' => 'filters',
          ],
        ];

        $element_operator = [
          '#type' => 'select',
          '#options' => $this->getOperatorsForColumn($column),
        ];

        $element_value = [
          '#type' => 'textfield',
        ];

        if ($filter_count > 0) {
          $element_remove = [
            '#type' => 'submit',
            '#value' => '',
            '#name' => 'removeFilter-' . $delta,
            '#submit' => ['::removeFilter'],
            '#attributes' => [
              'class' => ['glyphicon', 'glyphicon-remove'],
              'data-delta' => $delta,
            ],
            '#ajax' => [
              'callback' => '::removeFilterCallback',
              'wrapper' => 'filters',
            ],
          ];
          $form['filters'][$delta]['remove'] = $element_remove;
        }

        $form['filters'][$delta] = $element;
        $form['filters'][$delta]['column'] = $element_column;
        $form['filters'][$delta]['operator'] = $element_operator;
        $form['filters'][$delta]['value'] = $element_value;
      }
    }

    if (!isset($form['filters'][$filter_count - 1]['add'])) {
      $form['filters'][$filter_count - 1]['add'] = [
        '#type' => 'submit',
        '#value' => '',
        '#name' => 'addFilter',
        '#submit' => ['::addFilter'],
        '#attributes' => [
          'class' => ['glyphicon', 'glyphicon-plus'],
        ],
        '#ajax' => [
          'callback' => '::addFilterCallback',
          'wrapper' => 'filters',
        ],
      ];
    }

    $form['buttons'] = [
      '#type' => 'container',
    ];

    $form['buttons']['table'] = [
      '#type' => 'button',
      '#value' => 'table',
      '#name' => 'table',
      '#id' => 'query-table',
      '#ajax' => [
        'callback' => '::query',
      ],
    ];

    $form['buttons']['download'] = [
      '#type' => 'submit',
      '#value' => 'excel',
      '#name' => 'download',
      '#id' => 'download-query',
    ];

    $form['buttons']['map'] = [
      '#type' => 'button',
      '#value' => 'map',
      '#name' => 'map',
      '#id' => 'query-map',
      '#ajax' => [
        'callback' => '::map',
      ],
    ];

    $form['query_results'] = [
      '#type' => 'container',
      '#id' => 'query-results',
    ];

    return $form;
  }

  private function getDeployments($expedition_id) {
    $options = [];
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

        $options['0'] = "All Deployments";
        foreach ($expedition->{'deployments'} as $deployment) {
          $options[$deployment->{'deploymentId'}] = $deployment->{'deploymentId'};
        }
      }
      catch (RequestException $e) {
        watchdog_exception('arms', $e);
        drupal_set_message('Error fetching project.', 'error');
      }
    }

    return $options;
  }

  public function showDeploymentsCallback(array &$form, FormStateInterface $form_state) {
    return $form['toggle'];
  }

  public function addFilter(array &$form, FormStateInterface &$form_state) {
    $c = $form_state->get('filter_count') + 1;
    $form_state->set('filter_count', $c);
    $form_state->setRebuild(TRUE);
  }

  public function addFilterCallback(array &$form, FormStateInterface &$form_state) {
    return $form['filters'];
  }

  public function removeFilter(array &$form, FormStateInterface &$form_state) {
    $c = $form_state->get('filter_count') - 1;
    $form_state->set('filter_count', $c);

    $delta = $form_state->getTriggeringElement()['#attributes']['data-delta'];
    $last_filter = array_pop($form_state->getValue('filters'));

    $form_state->setValue([
      'filters',
      $delta,
      'column',
    ], $last_filter['column']);
    $form_state->setValue([
      'filters',
      $delta,
      'operator',
    ], $last_filter['operator']);
    $form_state->setValue(['filters', $delta, 'value'], $last_filter['value']);

    $form_state->setRebuild(TRUE);
  }

  public function removeFilterCallback(array &$form, FormStateInterface &$form_state) {
    return $form['filters'];
  }

  public function updateFilterOperatorsCallback(array &$form, FormStateInterface &$form_state) {
    return $form['filters'];
  }

  public function query(array $form, FormStateInterface $form_state) {
    $arms_config = \Drupal::config('arms.settings');
    $rest_root = $arms_config->get("arms_rest_uri");
    $response = new AjaxResponse();

    $client = \Drupal::service('http_client');
    try {
      $result = $client->post(
        $rest_root . 'deployments/query/json',
        [
          'accept' => 'application/json',
          'json' => ['criterion' => $this->getFilterCriteria($form, $form_state)],
        ]
      );
      $deployments = json_decode($result->getBody());
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      drupal_set_message('Error fetching query results.', 'error');
    }
    $response->addCommand(new HtmlCommand(
      '#query-results',
      [
        '#theme' => 'arms_query_results',
        '#deployments' => $deployments,
      ]
    ));

    $response->addCommand(new BootstrapSortableCommand());

    return $response;
  }

  public function map(array &$form, FormStateInterface $form_state) {
    $response = new AjaxResponse();
    $arms_config = \Drupal::config('arms.settings');
    $rest_root = $arms_config->get("arms_rest_uri");

    $client = \Drupal::service('http_client');

    try {
      $data = $client
        ->post(
          $rest_root . 'deployments/query/tab',
          [
            'accept' => 'application/octet-stream',
            'json' => ['criterion' => $this->getFilterCriteria($form, $form_state)],
          ]
        )
        ->getBody();
      $file = file_save_data($data);
      $file->setTemporary();

      $urltemplate = $arms_config->get("berkeley_mapper_template_uri");
      $formatter = new FormattableMarkup(
        $urltemplate,
        [
          '@configfile' => $arms_config->get('arms_berkeley_mapper_config_uri'),
          '@tabfile' => file_create_url($file->getFileUri()),
        ]
      );
      $url = (string) $formatter;
      $response->addCommand(new RedirectCommand($url, TRUE));
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      $response->addCommand(new PrependCommand(
        '#query-results',
        'Error fetching query results.'
      ));
    }

    return $response;
  }

  public function submitForm(array &$form, FormStateInterface $form_state) {
    $arms_config = \Drupal::config('arms.settings');
    $rest_root = $arms_config->get("arms_rest_uri");

    $client = \Drupal::service('http_client');

    try {
      $data = $client
        ->post(
          $rest_root . 'deployments/query/excel',
          [
            'accept' => 'application/octet-stream',
            'json' => ['criterion' => $this->getFilterCriteria($form, $form_state)],
          ]
        )
        ->getBody();

      header("Content-Disposition: attachment; filename=\"" . urlencode('arms-fims-output.xlsx') . "\"");
      header("Content-length: " . $data->getSize());

      echo $data;
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      drupal_set_message('Error fetching query results.', 'error');
    }
  }

  /**
   * create a Query json object from the form filter and deployments fields
   * @param array $form
   * @param \Drupal\Core\Form\FormStateInterface $form_state
   */
  private function getFilterCriteria(array &$form, FormStateInterface $form_state) {
    $filters = [];
    foreach ($form_state->getValue('filters') as $filter) {
      if (!empty($filter['value'])) {
        $criteria = [
          'key' => $filter['column'],
          'operator' => $filter['operator'],
          'value' => $filter['value'],
          'condition' => 'AND',
        ];
        array_push($filters, $criteria);
      }
    }

    $deployment_id_list = [];
    // 0 is all expeditions. only filter by deployments if searching a specific expedition
    if ($form_state->getValue("expeditions") != "0") {
      // 0 is all deployments
      if ($form_state->getValue("deployments")[0] == "0") {
        foreach ($form["toggle"]["deployments"]["#options"] as $deployment_id => $v) {
          if ($deployment_id != "0") {
            array_push($deployment_id_list, $deployment_id);
          }
        }
      }
      else {
        foreach ($form_state->getValue('deployments') as $deployment_id) {
          if ($deployment_id != "0") {
            array_push($deployment_id_list, $deployment_id);
          }
        }
      }
    }

    if (!empty($deployment_id_list)) {
      $criteria = [
        'key' => 'deploymentId',
        'operator' => 'IN',
        'value' => implode(",", $deployment_id_list),
        'condition' => 'AND',
      ];
      array_push($filters, $criteria);
    }

    return $filters;
  }

  private function getFilterColumnsForDisplay() {
    $filter_columns = ArmsFilterUtils::getFilterColumns();
    $filter_columns_display = [];

    foreach ($filter_columns as $column) {
      $filter_columns_display[$column->column_internal] = $column->column;
    }

    return $filter_columns_display;
  }

  private function getFilterColumnValue($delta, $form_state) {
    return $form_state->getValue([
      'filters',
      $delta,
      'column',
    ], ArmsFilterUtils::getFilterColumns()[0]->column_internal);
  }

  private function getOperatorsForColumn($column_internal) {
    $filter_columns = ArmsFilterUtils::getFilterColumns();
    $operators = ArmsFilterUtils::getFilterOperators();

    $datatype = "STRING";

    foreach ($filter_columns as $c) {
      if ($c->column_internal == $column_internal) {
        $datatype = $c->datatype;
      }
    }

    $column_operators = [];
    $column_operators_display = [];

    foreach ($operators as $operator) {
      if (key($operator) == $datatype) {
        $column_operators = array_merge([], current($operator));
        break;
      }
    }

    if (sizeof($column_operators) == 0) {
      // default to STRING datatype
      $column_operators = array_merge([], $operators[0]);
    }

    foreach ($column_operators as $operator) {
      $val = (array_key_exists($operator, $this->OPERATOR_MAP) ? $this->OPERATOR_MAP[$operator] : $operator);
      array_push($column_operators_display, $val);
    }

    return array_combine($column_operators, $column_operators_display);
  }
}
