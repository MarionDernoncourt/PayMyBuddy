<template>
  <div class="page-container">

    <div class="login-container">
      <h1>Pay My Buddy</h1>
      <form @submit.prevent="handleLogin" class="login-form">
        <input type="email" id="email" placeholder="Mail" v-model="email" required />
        <input type="password" id="password" placeholder="Mot de passe" v-model="password" required />
        <button type="submit">Se connecter</button>
      </form>
      <p v-if="error" style="color: red;">{{ error }}</p>
    </div>
  </div>
</template>

<script>
import AuthService from "@/services/AuthService"

export default {
  name: "LoginPage",
  components: {
  },
  data() {
    return {
      email: '',
      password: '',
      error: ''
    }
  },
  methods: {
    handleLogin() {
      AuthService.login(this.email, this.password)
        .then(response => {
          const token = response.data.token
          localStorage.setItem('token', token)
          const delay = 60 * 60 * 1000;
          localStorage.setItem('tokenExpiresAt', Date.now() + delay);
          localStorage.setItem('email', this.email);
          console.log("Connexion réussie :", response.data)

          this.$router.push('/transfer')
        })
        .catch(error => {
          this.error = "Échec de la connexion"
          console.error("Erreur de la connexion: ", error)
        })
    }
  }
}
</script>

<style>
.page-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-container {
  width: 50%;
  height: 70vh;
  border: 1px solid black;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  padding: 20px;
}

.login-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.input-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

input,
button {
  padding: 10px;
  margin: 10px;
  font-size: 16px;
  font-weight: bold;
}

button {
  font-weight: 600;
  font-size: 22px;
  padding: 15px;
  cursor: pointer;
  background-color: #207FEE;
  color: white;
  border-radius: 15px 15px 15px 15px;
  border: #207FEE 1px solid;
  ;
}

h1 {
  border-radius: 15px 15px 15px 15px;
  background-color: #F69F1D;
  color: white;
  padding: 15px;
  font-weight: 500;
  font-size: 22px;

}

/* Message d'erreur */
.error {
  color: red;
  margin-top: 10px;
}
</style>