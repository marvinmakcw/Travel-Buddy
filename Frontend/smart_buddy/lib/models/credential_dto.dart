import 'package:json_annotation/json_annotation.dart';

part 'credential_dto.g.dart';

/// Credential DTO for login requests
/// Matches the backend CredentialDto exactly
@JsonSerializable()
class CredentialDto {
  @JsonKey(name: 'username')
  final String username;
  
  @JsonKey(name: 'password')
  final String password;

  const CredentialDto({
    required this.username,
    required this.password,
  });

  /// Factory constructor for creating CredentialDto from JSON
  factory CredentialDto.fromJson(Map<String, dynamic> json) => _$CredentialDtoFromJson(json);

  /// Method for converting CredentialDto to JSON
  Map<String, dynamic> toJson() => _$CredentialDtoToJson(this);

  @override
  String toString() => 'CredentialDto(username: $username, password: ***)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is CredentialDto && 
           other.username == username && 
           other.password == password;
  }

  @override
  int get hashCode => username.hashCode ^ password.hashCode;
}
