<?php
/**
 * @file
 * Definition of \Drupal\arms_ckeditor\Plugin\CKEditorPlugin\Templates.
 */
namespace Drupal\arms_ckeditor\Plugin\CKEditorPlugin;
use Drupal\ckeditor\CKEditorPluginInterface;
use Drupal\ckeditor\CKEditorPluginButtonsInterface;
use Drupal\Component\Plugin\PluginBase;
use Drupal\editor\Entity\Editor;
/**
 * Defines the "Templates" plugin.
 *
 * @CKEditorPlugin(
 *   id = "templates",
 *   label = @Translation("Templates")
 * )
 */
class Templates extends PluginBase implements CKEditorPluginInterface, CKEditorPluginButtonsInterface {
  /**
   * Implements \Drupal\ckeditor\Plugin\CKEditorPluginInterface::getDependencies().
   */
  function getDependencies(Editor $editor) {
    return array();
  }
  /**
   * Implements \Drupal\ckeditor\Plugin\CKEditorPluginInterface::getLibraries().
   */
  function getLibraries(Editor $editor) {
    return array();
  }
  /**
   * Implements \Drupal\ckeditor\Plugin\CKEditorPluginInterface::isInternal().
   */
  function isInternal() {
    return FALSE;
  }
  /**
   * Implements \Drupal\ckeditor\Plugin\CKEditorPluginInterface::getFile().
   */
  function getFile() {
    return drupal_get_path('module', 'arms_ckeditor') . '/js/plugins/templates/plugin.js';
  }
  /**
   * Implements \Drupal\ckeditor\Plugin\CKEditorPluginButtonsInterface::getButtons().
   */
  function getButtons() {
    return array(
      'Templates' => array(
        'label' => t('Templates'),
        'image' => drupal_get_path('module', 'arms_ckeditor') . '/js/plugins/templates/icons/templates.png',
      )
    );
  }
  /**
   * Implements \Drupal\ckeditor\Plugin\CKEditorPluginInterface::getConfig().
   */
  public function getConfig(Editor $editor) {
    return array();
  }
}