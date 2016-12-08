<?php

/**
 * @file
 * Contains Drupal\fims_validation\Form\FimsValidationForm.
 */

namespace Drupal\fims_validation\Form;

use Drupal\Core\Form\FormBase;
use Drupal\Core\Form\FormStateInterface;

use GuzzleHttp\Client;
use Symfony\Component\DependencyInjection\ContainerInterface;

/**
 * Implements the SimpleForm form controller.
 *
 * This example demonstrates a simple form with a singe text input element. We
 * extend FormBase which is the simplest form base class used in Drupal.
 *
 * @see \Drupal\Core\Form\FormBase
 */
class FimsValidationForm extends FormBase {

  /**
   * @var \GuzzleHttp\Client
   */
  protected $fimsClient;

  /**
   * FimsValidationForm constructor.
   * @param \GuzzleHttp\Client $fims_client
   */
  public function __construct(Client $fims_client) {
    $this->fimsClient = $fims_client;
  }

  /**
   * {@inheritdoc}
   */
  public static function create(ContainerInterface $container) {
    return new static(
      $container->get('fims.client')
    );
  }

  /**
   * Build the simple form.
   *
   * A build form method constructs an array that defines how markup and
   * other form elements are included in an HTML form.
   *
   * @param array $form
   *   Default form array structure.
   * @param FormStateInterface $form_state
   *   Object containing current form state.
   *
   * @return array
   *   The render array defining the elements of the form.
   */
  public function buildForm(array $form, FormStateInterface $form_state) {

    // Disable caching on this form.
    $form_state->setCached(FALSE);

    $form['fims_dataset'] = [
      '#type' => 'file',
      '#title' => $this->t('FIMS Dataset'),
      '#description' => $this->t('Your fimsMetadata to validate/upload.'),
    ];

    $form['upload'] = [
      '#type' => 'checkbox',
      '#title' => $this->t('Upload'),
      '#description' => $this->t('Upload your fimsMetadata to Biocode-Fims.'),
    ];

    $form['expeditionCode'] = [
      '#type' => 'select',
      '#title' => 'Project Code',
      '#options' => $this->getExpeditionCodes(1),
      '#states' => [
        'visible' => [
          "input[name='upload']" => ['checked' => TRUE],
        ],
      ],
    ];

    $form['public'] = [
      '#type' => 'checkbox',
      '#title' => 'Public Project',
      '#states' => [
        'visible' => [
          "input[name='upload']" => ['checked' => TRUE],
        ],
      ],
    ];

    // Add a hidden projectId.
    $form['projectId'] = [
      '#type' => 'hidden',
      // TODO make this a configuration setting?
      '#value' => '1',
    ];

    // Group submit handlers in an actions element with a key of "actions" so
    // that it gets styled correctly, and so that other modules may add actions
    // to the form. This is not required, but is convention.
    $form['actions'] = [
      '#type' => 'actions',
    ];

    // Add a submit button that handles the submission of the form.
    $form['actions']['submit'] = [
      '#type' => 'submit',
      '#value' => $this->t('Submit'),
    ];

    return $form;
  }

  /**
   * Getter method for Form ID.
   *
   * The form ID is used in implementations of hook_form_alter() to allow other
   * modules to alter the render array built by this form controller.  it must
   * be unique site wide. It normally starts with the providing module's name.
   *
   * @return string
   *   The unique ID of the form defined by this class.
   */
  public function getFormId() {
    return 'fims_validation_form';
  }

  /**
   * Implements form validation.
   *
   * The validateForm method is the default method called to validate input on
   * a form.
   *
   * @param array $form
   *   The render array of the currently built form.
   * @param FormStateInterface $form_state
   *   Object describing the current state of the form.
   */
  public function validateForm(array &$form, FormStateInterface $form_state) {
    $title = $form_state->getValue('title');
    if (strlen($title) < 5) {
      // Set an error for the form element with a key of "title".
      $form_state->setErrorByName('title', $this->t('The title must be at least 5 characters long.'));
    }
  }

  /**
   * Implements a form submit handler.
   *
   * The submitForm method is the default method called for any submit elements.
   *
   * @param array $form
   *   The render array of the currently built form.
   * @param FormStateInterface $form_state
   *   Object describing the current state of the form.
   */
  public function submitForm(array &$form, FormStateInterface $form_state) {
    /*
     * This would normally be replaced by code that actually does something
     * with the title.
     */
    $title = $form_state->getValue('title');
    drupal_set_message(t('You specified a title of %title.', ['%title' => $title]));
  }

  /**
   * Fetch the expeditions belonging to a project
   *
   * @param $project_id
   * @return array containing expeditionCode => expeditionTitle pairs
   */
  private function getExpeditionCodes($project_id) {
    $expedition_select = ['0' => 'Create a new Expedition'];

    
    // try {
    // TODO move url to configuration
    // TODO centralize all rest calls?
    $response = $this->fimsClient->get(
      "projects/" . $project_id . "/expeditions"
    );
    $expeditions = json_decode($response->getBody(), TRUE);

    foreach ($expeditions as $expedition) {
      $expedition_select[$expedition["expeditionId"]] = $expedition["expeditionTitle"];
    }

    // } catch (RequestException $e) {
    //   return($this->t('Error'));
    // }

    return $expedition_select;
  }

}