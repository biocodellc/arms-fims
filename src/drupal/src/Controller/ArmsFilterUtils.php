<?php
/**
 * Created by IntelliJ IDEA.
 * User: rjewing
 * Date: 8/27/16
 * Time: 7:44 AM
 */

namespace Drupal\arms\Controller;


use GuzzleHttp\Exception\RequestException;

class ArmsFilterUtils {
  private static $filter_keys_cache_id = 'arms_cache_filter_keys';
  private static $filter_operators_cache_id = 'arms_cache_filter_operators';

  public static function getFilterColumns() {
    if ($cache = \Drupal::cache()->get(self::$filter_keys_cache_id)) {
      return $cache->data;
      // foreach ($cache->data as $option) {
      //   $filter_keys[$option->column_internal] = $option->column;
      // }
    }
    else {
      ArmsFilterUtils::setFilterOptions();
      return ArmsFilterUtils::getFilterColumns();
    }
  }

  public static function getFilterOperators() {
    if ($cache = \Drupal::cache()->get(self::$filter_operators_cache_id)) {
      $filter_operators = $cache->data;
    }
    else {
      self::setFilterOptions();
      return self::getFilterOperators();
    }
    return $filter_operators;
  }

  private static function setFilterOptions() {
    $arms_config = \Drupal::config('arms.settings');
    $rest_root = $arms_config->get("arms_rest_uri");

    $client = \Drupal::service('http_client');
    try {
      $filter_keys = [];

      $result = $client->get(
        $rest_root . 'projects/filterOptions',
        ['Accept' => 'application/json']
      );
      $data = json_decode($result->getBody());

      foreach ($data->keys as $option) {
        if ($option->column != 'deploymentID') {
          array_push($filter_keys, $option);
          // $filter_keys[$option->column_internal] = $option;
        }
      }

      \Drupal::cache()->set(self::$filter_keys_cache_id, $filter_keys);
      \Drupal::cache()
        ->set(self::$filter_operators_cache_id, $data->operators);
    }
    catch (RequestException $e) {
      watchdog_exception('arms', $e);
      drupal_set_message('Error fetching filter options.', 'error');
    }
  }

}