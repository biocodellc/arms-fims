<?php

namespace Drupal\arms\Ajax;
use Drupal\Core\Ajax\CommandInterface;

class RedirectCommand implements CommandInterface {

  public function __construct($url, $new_window = FALSE) {
    $this->url = $url;
    $this->new_window = $new_window;
  }

  public function render() {
    return [
      'command' => 'redirect',
      'url' => $this->url,
      'newWindow' => $this->new_window,
    ];
  }
}