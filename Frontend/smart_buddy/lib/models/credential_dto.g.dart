// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'credential_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CredentialDto _$CredentialDtoFromJson(Map<String, dynamic> json) =>
    CredentialDto(
      username: json['username'] as String,
      password: json['password'] as String,
    );

Map<String, dynamic> _$CredentialDtoToJson(CredentialDto instance) =>
    <String, dynamic>{
      'username': instance.username,
      'password': instance.password,
    };
