/// Custom authentication exceptions that correspond to backend errors
/// These help provide specific error messages to users
library;

/// Thrown when user credentials are not found (HTTP 404)
class UserNotFoundException implements Exception {
  final String message;
  UserNotFoundException(this.message);
  
  @override
  String toString() => message;
}

/// Thrown when password is incorrect (HTTP 401)
class WrongPasswordException implements Exception {
  final String message;
  WrongPasswordException(this.message);
  
  @override
  String toString() => message;
}

/// Thrown when there are network connectivity issues
class NetworkException implements Exception {
  final String message;
  NetworkException(this.message);
  
  @override
  String toString() => message;
}

/// Thrown for other server errors (HTTP 500, etc.)
class ServerException implements Exception {
  final String message;
  ServerException(this.message);
  
  @override
  String toString() => message;
}

/// Generic authentication exception for unexpected errors
class AuthException implements Exception {
  final String message;
  AuthException(this.message);
  
  @override
  String toString() => message;
}
