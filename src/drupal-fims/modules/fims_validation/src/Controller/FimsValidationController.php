<?php

/**
 * @file
 * Contains \Drupal\fims_validation\Controller\FimsValidationController.
 */

namespace Drupal\fims_validation\Controller;

use Drupal\Core\Controller\ControllerBase;

class FimsValidationController extends ControllerBase {
  public function vaildation_page() {
    var_dump("here");
    return array(
      '#type' => 'markup',
      '#markup' => $this->t('Welcome to the Fims validation Interface!'),
    );
  }
}