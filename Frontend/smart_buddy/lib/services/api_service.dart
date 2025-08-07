import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../models/api_response.dart';
import '../models/credential_dto.dart';
import '../models/token_dto.dart';
import '../exceptions/auth_exceptions.dart';

/// Service for handling all API communications with the backend
/// Handles authentication endpoints and future API calls
class ApiService {
  // Backend URL - Using PC's IP address for Android emulator to access host machine
  // Alternative addresses:
  // 'http://localhost:8080' - for web/desktop testing
  // 'http://10.0.2.2:8080' - Android emulator localhost mapping
  static const String baseUrl = 'http://10.0.2.2:8080';
  static const String authEndpoint = '/smart_buddy/auth/tokens';
  
  /// Login method that sends credentials to backend
  /// Returns TokenDto on success, throws appropriate exception on failure
  Future<TokenDto> login(String username, String password) async {
    try {
      final credential = CredentialDto(username: username, password: password);
      final url = Uri.parse('$baseUrl$authEndpoint');
      
      final response = await http.post(
        url,
        headers: {
          'Content-Type': 'application/json',
        },
        body: jsonEncode(credential.toJson()),
      );

      // Parse response based on status code
      if (response.statusCode == 200) {
        final responseBody = jsonDecode(response.body) as Map<String, dynamic>;
        final apiResponse = ApiResponse<Map<String, dynamic>>.fromJson(
          responseBody,
          (json) => json as Map<String, dynamic>,
        );
        
        if (apiResponse.data != null) {
          return TokenDto.fromJson(apiResponse.data!);
        } else {
          throw ServerException('No token data received from server');
        }
      } else if (response.statusCode == 404) {
        // User not found
        final responseBody = jsonDecode(response.body) as Map<String, dynamic>;
        final apiResponse = ApiResponse<dynamic>.fromJson(
          responseBody,
          (json) => json,
        );
        throw UserNotFoundException(apiResponse.message);
      } else if (response.statusCode == 401) {
        // Wrong password
        final responseBody = jsonDecode(response.body) as Map<String, dynamic>;
        final apiResponse = ApiResponse<dynamic>.fromJson(
          responseBody,
          (json) => json,
        );
        throw WrongPasswordException(apiResponse.message);
      } else {
        // Other server errors
        throw ServerException('Server error: ${response.statusCode}');
      }
    } on SocketException catch (e) {
      throw NetworkException('No internet connection. Please check your network and try again. Error: ${e.message}');
    } on FormatException catch (e) {
      throw ServerException('Invalid response format from server: ${e.message}');
    } on UserNotFoundException {
      rethrow;
    } on WrongPasswordException {
      rethrow;
    } on NetworkException {
      rethrow;
    } on ServerException {
      rethrow;
    } catch (e) {
      throw AuthException('An unexpected error occurred: $e');
    }
  }

  /// Helper method for making authenticated API requests
  /// Use this for future API calls that require authentication
  Future<http.Response> authenticatedRequest({
    required String endpoint,
    required String method,
    required Map<String, String> headers,
    String? body,
  }) async {
    final url = Uri.parse('$baseUrl$endpoint');
    
    switch (method.toUpperCase()) {
      case 'GET':
        return await http.get(url, headers: headers);
      case 'POST':
        return await http.post(url, headers: headers, body: body);
      case 'PUT':
        return await http.put(url, headers: headers, body: body);
      case 'DELETE':
        return await http.delete(url, headers: headers);
      default:
        throw ArgumentError('Unsupported HTTP method: $method');
    }
  }
}
