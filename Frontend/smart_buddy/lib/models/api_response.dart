import 'package:json_annotation/json_annotation.dart';

part 'api_response.g.dart';

/// Generic API response wrapper that matches backend ApiResponse<T>
/// Used to wrap all API responses with status, message, and data
@JsonSerializable(genericArgumentFactories: true)
class ApiResponse<T> {
  @JsonKey(name: 'status')
  final int status;
  
  @JsonKey(name: 'message')
  final String message;
  
  @JsonKey(name: 'data')
  final T? data;

  const ApiResponse({
    required this.status,
    required this.message,
    this.data,
  });

  /// Factory constructor for creating ApiResponse from JSON
  factory ApiResponse.fromJson(
    Map<String, dynamic> json,
    T Function(Object? json) fromJsonT,
  ) => _$ApiResponseFromJson(json, fromJsonT);

  /// Method for converting ApiResponse to JSON
  Map<String, dynamic> toJson(Object Function(T value) toJsonT) =>
      _$ApiResponseToJson(this, toJsonT);

  /// Check if the response indicates success
  bool get isSuccess => status >= 200 && status < 300;

  /// Check if the response indicates an error
  bool get isError => !isSuccess;

  @override
  String toString() => 'ApiResponse(status: $status, message: $message, data: $data)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is ApiResponse<T> &&
           other.status == status &&
           other.message == message &&
           other.data == data;
  }

  @override
  int get hashCode => status.hashCode ^ message.hashCode ^ data.hashCode;
}
