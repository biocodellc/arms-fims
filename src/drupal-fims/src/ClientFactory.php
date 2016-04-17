<?php

namespace Drupal\fims;

use CommerceGuys\Guzzle\Oauth2\GrantType\PasswordCredentials;
use CommerceGuys\Guzzle\Oauth2\GrantType\RefreshToken;
use CommerceGuys\Guzzle\Oauth2\Middleware\OAuthMiddleware;
use GuzzleHttp\Client;
use GuzzleHttp\HandlerStack;

class ClientFactory {

  /**
   * Return a configured Client object.
   */
  public function get() {
    $fims_config = \Drupal::config('fims.settings');

    $handler_stack = HandlerStack::create();
    $client = new Client([
      'handler' => $handler_stack,
      'base_uri' => $fims_config->get('fims_rest_uri'),
      'auth' => 'oauth2',
    ]);

    $config = [
      'username' => 'demo',
      'password' => 'demo',
      'token_url' => $fims_config->get('fims_rest_uri') . 'authenticationService/oauth/accessToken',
      'client_id' => $fims_config->get('oauth2_client_id'),
      'client_secret' => $fims_config->get('oauth2_client_secret'),
    ];

    $refresh_config = $config;
    $refresh_config['token_url'] = $fims_config->get('fims_rest_uri') . 'authenticationService/oauth/refreshToken';

    $token = new PasswordCredentials($client, $config);
    $refresh_token = new RefreshToken($client, $refresh_config);
    $middleware = new OAuthMiddleware($client, $token, $refresh_token);

    $handler_stack->push($middleware->onBefore());
    $handler_stack->push($middleware->onFailure(1));

    return $client;
  }
}
