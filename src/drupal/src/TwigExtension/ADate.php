<?php

namespace Drupal\arms\TwigExtension;

use DateTime;

class ADate extends \Twig_Extension {

  public function getTests() {
    return [
      new \Twig_SimpleTest('a_date', [$this, 'aDate']),
    ];
  }

  /**
   * Returns the name of the extension.
   *
   * @return string The extension name
   */
  public function getName() {
    return 'arms_is_date.twig_extension';
  }

  public static function aDate($string) {
    return (bool)strtotime($string);
  }
}
