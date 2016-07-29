<?php

namespace Drupal\arms\Plugin\Block;

use Drupal\Core\Block\BlockBase;
use Drupal\Core\Form\FormStateInterface;
use Drupal\file\Entity\File;

/**
 * Provides a bootstrap carousel block
 *
 * @Block(
 *   id = "carousel_block",
 *   admin_label = @Translation("Carousel Block")
 * )
 */
class CarouselBlock extends BlockBase {

  /**
   * {@inheritdoc}
   */
  public function build() {
    $config = $this->getConfiguration();

    $width = isset($config['width']) ? $config['width'] : '100%';
    $height = isset($config['height']) ? $config['height'] : '100%';
    $id = isset($config['id']) ? $config['id'] : '';

    $imgs = [];

    foreach ($config['img'] as $img_id) {
      $file = File::load($img_id);
      array_push($imgs, file_create_url($file->getFileUri()));
    }

    return [
      '#theme' => 'block--carouselblock',
      '#imgs' => $imgs,
      '#width' => $width,
      '#height' => $height,
      '#id' => $id,
    ];
  }

  /**
   * {@inheritdoc}
   */
  public function blockForm($form, FormStateInterface $form_state) {
    $form = parent::blockForm($form, $form_state);

    // Retrieve existing configuration for this block.
    $config = $this->getConfiguration();

    // Add a form field to the existing block configuration form.
    $form['img'] = [
      '#type' => 'managed_file',
      '#title' => t('Images'),
      '#description' => t('Image(s) to be displayed in the carousel'),
      '#multiple' => TRUE,
      '#required' => TRUE,
      '#default_value' => isset($config['img']) ? $config['img'] : '',
      '#upload_location' => 'public://images/carousel',
    ];

    $form['width'] = array(
      '#type' => 'textfield',
      '#title' => t('Width'),
      '#default_value' => isset($config['width']) ? $config['width'] : '100%',
      '#description' => t('Width of the carousel. Must include units ex.(%, px, em)'),
    );

    $form['height'] = array(
      '#type' => 'textfield',
      '#title' => t('Height'),
      '#default_value' => isset($config['height']) ? $config['height'] : '100%',
      '#description' => t('Height of the carousel. Must include units ex.(%, px, em)'),
    );

    $form['id'] = array(
      '#type' => 'textfield',
      '#title' => t('Carousel container id'),
      '#default_value' => isset($config['id']) ? $config['id'] : '',
      '#description' => t('id of the carousel container. Used for css styling'),
    );
    return $form;
  }

  /**
   * {@inheritdoc}
   */
  public function blockSubmit($form, FormStateInterface $form_state) {
    // Save our custom settings when the form is submitted.
    $this->setConfigurationValue('width', $form_state->getValue('width'));
    $this->setConfigurationValue('height', $form_state->getValue('height'));
    $this->setConfigurationValue('id', $form_state->getValue('id'));
    $this->setConfigurationValue('img', $form_state->getValue('img'));
  }

}
