<template>
  <div class="page-container">
    <form @submit.prevent="searchFriend" class="search-form">
      <div class="input-container">
        <label for="friendEmail">Chercher une relation</label>
        <input type="email" id="friendEmail" placeholder="Saisir une adresse email" v-model="email" required />
      </div>
      <button class="button-submit" type="submit">Ajouter</button>

    </form>
          <p class="error-message" :class="{ error: isError }">{{ message }}</p>

  </div>
</template>

<script>

import axios from "axios";

export default {
  name: "AddFriend",
  data() {
    return {
      email: "",
      message: "",
      isError: false,
    };
  },
  methods: {
    async searchFriend() {
      try {
        const response = await axios.post(
          "http://localhost:8081/api/users/addfriends",
          { email: this.email },
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json'  // optionnel mais conseillé
            },
            withCredentials: true
          }
        );
        if (response.data.email) {
          this.message = "Relation trouvée";
          this.isError = false;
          this.email = "";
        } else {
          this.message = "Aucun utilisateur trouvé avec cet email";
          this.isError = true;
        }
      } catch (error) {
        this.message = "Erreur lors de la recherche.";
        this.isError = true;
        console.error("Erreur ajout ami : ", error.response?.status, error.response?.data);
      }
    }
  },
  mounted() {
    if (!localStorage.getItem("token")) {
      this.$router.push("/login")
    }
  }
}
</script>

<style>
.search-form {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

label {
  text-align: left;
  width: 100%;
}

.input-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.button-submit {
  border-radius: 15px 15px 15px 15px;
  background-color: #F69F1D;
  color: white;
  padding: 15px;
  font-weight: 500;
  font-size: 22px;
  border: #F69F1D 1px solid;
}

.error-message {
  color: red;

}
</style>