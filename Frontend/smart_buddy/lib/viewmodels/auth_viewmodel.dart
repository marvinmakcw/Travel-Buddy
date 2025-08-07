import 'package:flutter/foundation.dart';
import '../models/user.dart';
import '../services/api_service.dart';
import '../services/storage_service.dart';
import '../exceptions/auth_exceptions.dart';

/// Authentication states that the app can be in
enum AuthState {
  initial,        // App just started, checking auth status
  loading,        // Processing login/logout operations
  authenticated,  // User is logged in successfully
  unauthenticated,// User needs to login
  error,          // An error occurred during authentication
}

/// ViewModel for managing authentication state and operations
/// Uses ChangeNotifier for state management with Provider pattern
class AuthViewModel extends ChangeNotifier {
  // Services
  final ApiService _apiService = ApiService();
  final StorageService _storageService = StorageService();

  // Private state variables
  AuthState _state = AuthState.initial;
  User? _currentUser;
  String? _errorMessage;

  // Public getters
  AuthState get state => _state;
  User? get currentUser => _currentUser;
  String? get errorMessage => _errorMessage;
  bool get isAuthenticated => _state == AuthState.authenticated;
  bool get isLoading => _state == AuthState.loading;
  bool get hasError => _state == AuthState.error;

  /// Initialize the authentication state
  /// Checks if user is already logged in from previous session
  Future<void> initialize() async {
    try {
      _setState(AuthState.loading);
      
      // Check if user is already logged in
      final isLoggedIn = await _storageService.isLoggedIn();
      
      if (isLoggedIn) {
        // Retrieve saved user data
        final user = await _storageService.getUser();
        if (user != null) {
          _currentUser = user;
          _setState(AuthState.authenticated);
        } else {
          // If user data is corrupted, clear auth data
          await _storageService.clearAuthData();
          _setState(AuthState.unauthenticated);
        }
      } else {
        _setState(AuthState.unauthenticated);
      }
    } catch (e) {
      _setError('Failed to initialize authentication: $e');
    }
  }

  /// Login with username and password
  Future<void> login(String username, String password) async {
    try {
      _setState(AuthState.loading);
      _clearError();

      // Validate input
      if (username.trim().isEmpty || password.trim().isEmpty) {
        throw AuthException('Username and password cannot be empty');
      }

      // Call API to authenticate
      final tokenDto = await _apiService.login(username.trim(), password);
      
      // Save token
      await _storageService.saveToken(tokenDto.token);
      
      // Create and save user object
      // Note: In real app, you might want to decode JWT to get userId
      // For now, we'll use a placeholder userId
      final user = User(
        userId: 1001, // This should come from JWT token or separate API call
        username: username.trim(),
      );
      
      await _storageService.saveUser(user);
      
      // Update state
      _currentUser = user;
      _setState(AuthState.authenticated);
      
    } on UserNotFoundException {
      _setError('User not found. Please check your username.');
    } on WrongPasswordException {
      _setError('Incorrect password. Please try again.');
    } on NetworkException {
      _setError('Network error. Please check your connection and try again.');
    } on ServerException {
      _setError('Server error. Please try again later.');
    } on AuthException catch (e) {
      _setError(e.message);
    } catch (e) {
      _setError('An unexpected error occurred. Please try again.');
    }
  }

  /// Logout the current user
  Future<void> logout() async {
    try {
      _setState(AuthState.loading);
      
      // Clear all stored authentication data
      await _storageService.clearAuthData();
      
      // Reset state
      _currentUser = null;
      _clearError();
      _setState(AuthState.unauthenticated);
      
    } catch (e) {
      _setError('Failed to logout: $e');
    }
  }

  /// Clear any error messages
  void clearError() {
    _clearError();
    if (_state == AuthState.error) {
      _setState(_currentUser != null ? AuthState.authenticated : AuthState.unauthenticated);
    }
  }

  // Private helper methods
  void _setState(AuthState newState) {
    if (_state != newState) {
      _state = newState;
      notifyListeners();
    }
  }

  void _setError(String message) {
    _errorMessage = message;
    _setState(AuthState.error);
  }

  void _clearError() {
    _errorMessage = null;
  }

}
