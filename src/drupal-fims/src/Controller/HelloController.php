<?php

/**
 * @file
 * Contains \Drupal\fims\Controller\HelloController.
 */

namespace Drupal\fims\Controller;

use Drupal\Core\Controller\ControllerBase;

class HelloController extends ControllerBase {
  public function content() {
    return array(
      '#type' => 'markup',
      // '#markup' => $this->t('Hello, World!'),
      '#markup' => $this->t(phpinfo()),
    );
  }
}
