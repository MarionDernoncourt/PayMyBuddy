<template>
  <div class="page-container">
    <div class="register-container">
      <h1>Pay My Buddy</h1>
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="input-container"> <input type="username" id="username" placeholder="Username" v-model="username"
            required />

          <input type="email" id="email" placeholder="email" v-model="email" required />

          <input type="password" id="password" placeholder="password" v-model="password" required />

        </div>
        <button type="submit">S'inscrire</button>

      </form>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script>
import AuthService from "@/services/AuthService";

export default {
  name: "registerPage",
  data() {
    return {
      username: '',
      email: '',
      password: '',
      errorMessage: ''
    };
  },
  methods: {
    handleRegister() {
      AuthService.register(this.username, this.email, this.password)
        .then(response => {
          console.log("Inscription réussie :", response.data);
          this.$router.push("/login");
        })
        .catch(error => {
          this.errorMessage = "Erreur lors de l'inscription, vérifiez vos informations"
          console.error("Erreur inscription:", error);
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

.register-container {
  width: 50%;
  height: 70vh;
  border: 1px solid black;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  padding: 20px;
}

.register-form {
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
  border:#207FEE 1px solid;
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