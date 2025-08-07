import 'package:json_annotation/json_annotation.dart';

part 'user.g.dart';

/// User model representing the logged-in user
/// Corresponds to the User entity in the backend
@JsonSerializable()
class User {
  @JsonKey(name: 'userId')
  final int userId;
  
  @JsonKey(name: 'username')
  final String username;

  const User({
    required this.userId,
    required this.username,
  });

  /// Factory constructor for creating User from JSON
  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);

  /// Method for converting User to JSON
  Map<String, dynamic> toJson() => _$UserToJson(this);

  @override
  String toString() => 'User(userId: $userId, username: $username)';

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is User && other.userId == userId && other.username == username;
  }

  @override
  int get hashCode => userId.hashCode ^ username.hashCode;
}
