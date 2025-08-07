import 'package:json_annotation/json_annotation.dart';

part 'token_dto.g.dart';

/// Token DTO that matches the backend TokenDto
/// Contains the JWT token received after successful authentication
@JsonSerializable()
class TokenDto {
  @JsonKey(name: 'token')
  final String token;

  const TokenDto({required this.token});

  /// Factory constructor for creating TokenDto from JSON
  factory TokenDto.fromJson(Map<String, dynamic> json) => _$TokenDtoFromJson(json);

  /// Method for converting TokenDto to JSON
  Map<String, dynamic> toJson() => _$TokenDtoToJson(this);

  @override
  String toString() => 'TokenDto(token: ${token.substring(0, 20)}...)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is TokenDto && other.token == token;
  }

  @override
  int get hashCode => token.hashCode;
}
