<?php

namespace Drupal\arms\TwigExtension;

use Drupal\Core\Url;
use Twig_SimpleFunction;

class UrlPath extends \Twig_Extension {

  /**
   * {@inheritdoc}
   */
  public function getFunctions() {
    return [
      new Twig_SimpleFunction('url_path', array($this, 'getPathFromUrl'))
    ];
  }

  /**
   * Returns the name of the extension.
   *
   * @return string The extension name
   */
  public function getName() {
    return 'arms_url_path.twig_extension';
  }

  /**
   * Gets a path from Url object.
   *
   * @param \Drupal\Core\Url $url
   *   The URL object to retrieve the path from.
   *
   * @return string
   *   Path to where the Url object is poiting to.
   */
  public static function getPathFromUrl(Url $url) {
    return $url->toString();
  }
}
