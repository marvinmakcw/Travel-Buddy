import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:go_router/go_router.dart';
import 'viewmodels/auth_viewmodel.dart';
import 'screens/splash_screen.dart';
import 'screens/login_screen.dart';
import 'screens/home_screen.dart';

void main() {
  runApp(const SmartBuddyApp());
}

class SmartBuddyApp extends StatelessWidget {
  const SmartBuddyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthViewModel()),
      ],
      child: Consumer<AuthViewModel>(
        builder: (context, authViewModel, child) {
          return MaterialApp.router(
            title: 'Smart Buddy',
            debugShowCheckedModeBanner: false,
            theme: ThemeData(
              colorScheme: ColorScheme.fromSeed(
                seedColor: Colors.blue,
                brightness: Brightness.light,
              ),
              useMaterial3: true,
              appBarTheme: const AppBarTheme(
                centerTitle: true,
                elevation: 0,
              ),
            ),
            routerConfig: _createRouter(authViewModel),
          );
        },
      ),
    );
  }

  GoRouter _createRouter(AuthViewModel authViewModel) {
    return GoRouter(
      initialLocation: '/splash',
      redirect: (context, state) {
        final isInitialized = authViewModel.state != AuthState.initial;
        final isAuthenticated = authViewModel.isAuthenticated;
        final isOnSplash = state.matchedLocation == '/splash';
        final isOnLogin = state.matchedLocation == '/login';
        
        // Show splash screen while initializing
        if (!isInitialized && !isOnSplash) {
          return '/splash';
        }
        
        // After initialization, redirect based on auth state
        if (isInitialized) {
          if (isAuthenticated && (isOnLogin || isOnSplash)) {
            return '/home';
          }
          if (!isAuthenticated && !isOnLogin) {
            return '/login';
          }
        }
        
        return null; // No redirect needed
      },
      routes: [
        GoRoute(
          path: '/splash',
          builder: (context, state) {
            // Trigger initialization when splash screen is shown
            WidgetsBinding.instance.addPostFrameCallback((_) {
              if (authViewModel.state == AuthState.initial) {
                authViewModel.initialize();
              }
            });
            return const SplashScreen();
          },
        ),
        GoRoute(
          path: '/login',
          builder: (context, state) => const LoginScreen(),
        ),
        GoRoute(
          path: '/home',
          builder: (context, state) => const HomeScreen(),
        ),
      ],
    );
  }
}
