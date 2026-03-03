package com.samscloak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samscloak.data.repository.AuthRepository
import com.samscloak.data.repository.AuthResult
import com.samscloak.network.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: LoginResponse? = null
)

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState(isLoading = true)
            
            when (val result = authRepository.login(email, password)) {
                is AuthResult.Success -> {
                    _loginState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = true,
                        user = result.data
                    )
                }
                is AuthResult.Error -> {
                    _loginState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = false,
                        error = result.message
                    )
                }
                is AuthResult.Loading -> {
                    _loginState.value = LoginUiState(isLoading = true)
                }
            }
        }
    }
    
    fun register(email: String, password: String, fullName: String?) {
        viewModelScope.launch {
            _registerState.value = RegisterUiState(isLoading = true)
            
            when (val result = authRepository.register(email, password, fullName)) {
                is AuthResult.Success -> {
                    _registerState.value = RegisterUiState(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is AuthResult.Error -> {
                    _registerState.value = RegisterUiState(
                        isLoading = false,
                        isSuccess = false,
                        error = result.message
                    )
                }
                is AuthResult.Loading -> {
                    _registerState.value = RegisterUiState(isLoading = true)
                }
            }
        }
    }
    
    fun clearError() {
        _loginState.value = _loginState.value.copy(error = null)
        _registerState.value = _registerState.value.copy(error = null)
    }
    
    fun setError(message: String) {
        _registerState.value = _registerState.value.copy(error = message)
    }
    
    fun resetState() {
        _loginState.value = LoginUiState()
        _registerState.value = RegisterUiState()
    }
    
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
    
    fun logout() {
        authRepository.logout()
        _loginState.value = LoginUiState()
    }
}
