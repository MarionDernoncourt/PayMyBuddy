<template>
  <div class="edit-user">
    <div class="form-row">
      <label>Username</label>
      <span v-if="!editing.username" @click="editField('username')">{{ form.username }}</span>
      <input v-else v-model="form.username" ref="usernameInput" blur="editing.username = false" />
    </div>

    <div class="form-row">
      <label>Mail</label>
      <span v-if="!editing.email" @click="editField('email')">{{ form.email }}</span>
      <input v-else v-model="form.email" ref="emailInput" @blur="editing.email = false" />
    </div>

    <div class="form-row">
      <label>Mot de passe</label>
      <span v-if="!editing.password" @click="editField('password')">********</span>
      <input v-else type="password" v-model="form.password" ref="passwordInput" @blur="editing.password = false" />
    </div>

    <button @click="submitForm">Modifier</button>

    <p v-if="message" :class="{ error: isError }">{{ message }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      form: {
        username: '',
        email: '',
        password: '*********'
      },
      editing: {
        username: false,
        email: false,
        password: false
      },
      message: '',
      isError: false
    };
  },
  created() {
    this.fetchProfil();
  },
  methods: {
    fetchProfil() {
      axios
        .get("http://localhost:8081/api/users/profil", {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          },
          withCredentials: true
        })
        .then(res => {
          this.form = res.data;
        })
        .catch( () => {
          this.message = "Erreur lors de la récupération de votre profil";
          this.isError = true;
        });
    },
    editField(field) {
      this.editing[field] = true;
      this.$nextTick(() => {
        this.$refs[`${field}Input`]?.focus();
      });
    },   
    submitForm() {
const payload = {
      username: this.form.username,
      email: this.form.email,
      password: this.form.password
    };
axios.post('http://localhost:8081/api/users/updateProfil', payload, {
  headers: {
    Authorization: `Bearer ${localStorage.getItem('token')}`,
    'Content-Type': 'application/json'
  },
  withCredentials: true
})
.then((res) => {
    const {username, email, token} = res.data;


  if(token) {
    localStorage.removeItem('token');
    localStorage.setItem('token', token);
  }
  this.form.username = username, 
  this.form.email = email, 
  this.password = '*********';

   this.editing.username = false;
    this.editing.email = false;
    this.editing.password = false;

    })
.catch(error => {
  this.isError = true;
  if(error.response && error.response.data) {
    this.message = error.response.data.message || "Une erreur est survenue lors de la mise à jour de votre profil"
  } else {
    this.message = " Erreur de connexion au serveur";
  }
})
    }
  }
};
</script>

<style scoped>
.edit-user {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.edit-user div {
  margin-bottom: 1rem;
}

.form-row{
  width: 35%;
  text-align: left;
  display: flex;
  flex-direction: row;
  justify-content: center;
}
label {
  width: 50%;
  font-weight: bold;
  text-align: left;
}
span {
  width: 50%;
}
input {
  padding: 5px;
}
span {
  cursor: pointer;
  padding: 5px;
  display: inline-block;
}
button {
  padding: 8px 16px;
  background-color: #F69F1D;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.error {
  color: red;
}
</style>
