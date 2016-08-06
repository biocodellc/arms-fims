<?php

namespace Drupal\arms\Ajax;
use Drupal\Core\Ajax\CommandInterface;

class BootstrapSortableCommand implements CommandInterface {

  public function __construct($options = NULL) {
    $this->options = $options;
  }

  public function render() {
    return [
      'command' => 'bootstrapSortable',
      'options' => $this->options,
    ];
  }
}