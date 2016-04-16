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
    $base_uri = 'localhost:8080/';

    $handler_stack = HandlerStack::create();
    $client = new Client([
      'handler' => $handler_stack,
      'base_uri' => $base_uri,
      'auth' => 'oauth2',
    ]);

    $config = [
      'username' => '',
      'password' => '',
      'token_url' => 'biocode-fims/rest/authenticationService/oauth/accessToken',
      'client_id' => '',
      'client_secret' => '',
    ];

    $refresh_config = $config;
    $refresh_config['token_url'] = 'biocode-fims/rest/authenticationService/oauth/refreshToken';

    $token = new PasswordCredentials($client, $config);
    $refresh_token = new RefreshToken($client, $refresh_config);
    $middleware = new OAuthMiddleware($client, $token, $refresh_token);

    $handler_stack->push($middleware->onBefore());
    $handler_stack->push($middleware->onFailure(5));

    return $client;
  }
}
