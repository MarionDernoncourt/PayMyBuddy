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
          this.message = "Relation ajoutée";
          this.isError = false;
          this.email = "";
        }
      } catch (error) {
        if (error.response?.data?.email == "Aucun utilisateur trouvé") {
          this.message = "Aucun utilisateur trouvé";
          this.isError = true;
        } else if (error.response?.data?.email == "Cet utilisateur est déjà dans votre liste d'amis") {

          this.message = "Cet utilisateur est déjà dans votre liste d'amis";
          this.isError = true;
          console.error("Erreur ajout ami : ", error.response?.status, error.response?.data);
        }
        else {
          this.message = "Une erreur est survenue lors de l'ajout de votre relation";
          this.isError = true;
        }
      }
    },
    mounted() {
      if (!localStorage.getItem("token")) {
        this.$router.push("/login")
      }
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