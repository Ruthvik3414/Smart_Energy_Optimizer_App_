package com.example.smart_energy_optimizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignInScreen(vm: EnergyViewModel) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Purple)
        Text("Sign in to continue", fontSize = 14.sp, color = DarkMuted)
        
        Spacer(Modifier.height(32.dp))

        AuthField(label = "Email or Phone", value = emailOrPhone, onValueChange = { emailOrPhone = it })
        Spacer(Modifier.height(16.dp))
        AuthField(label = "Password", value = password, onValueChange = { password = it }, isPassword = true)

        if (vm.authError != null) {
            Spacer(Modifier.height(8.dp))
            Text(vm.authError!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { vm.login(emailOrPhone, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign In", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { vm.navigateToSignUp() }) {
            Text("Don't have an account? Sign Up", color = Purple, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun SignUpScreen(vm: EnergyViewModel) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Purple)
        Text("Start your energy saving journey", fontSize = 14.sp, color = DarkMuted)

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) { AuthField("First Name", firstName) { firstName = it } }
            Box(Modifier.weight(1f)) { AuthField("Last Name", lastName) { lastName = it } }
        }
        Spacer(Modifier.height(16.dp))
        AuthField("Email Address", email, keyboardType = KeyboardType.Email) { email = it }
        Spacer(Modifier.height(16.dp))
        AuthField("Phone Number", phone, keyboardType = KeyboardType.Phone) { phone = it }
        Spacer(Modifier.height(16.dp))
        AuthField("Password", password, isPassword = true) { password = it }

        if (vm.authError != null) {
            Spacer(Modifier.height(8.dp))
            Text(vm.authError!!, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { vm.signUp(firstName, lastName, email, phone, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Create Account", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = { vm.navigateToSignIn() }) {
            Text("Already have an account? Sign In", color = Purple, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AuthField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkMuted)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
