<template>
  <div class="header">
    <div class="title">
      <p>Pay My Buddy</p>
      <nav class="nav-links">
        <router-link to="transfer">Transferer</router-link>
        <router-link to="edit-profile">Profil</router-link>
        <router-link to="addFriend">Ajouter relation</router-link>
        <a @click.prevent="logout" href="#">Se déconnecter</a>
      </nav>
    </div>
  </div>
</template>

<script>

export default {
  name: "headerComponent",
  methods: {
    logout() {
      localStorage.removeItem("token")
      localStorage.removeItem("tokenExpiresAt")
      localStorage.removeItem("email")

      this.$emit('logout'); // notification vers App.vue
      this.$router.push("/login")
    }
  }
}
</script>

<style scoped>
.title {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background-color: white;

  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 0 30px;
  z-index: 999;
}



.nav-links {
  display: flex;
  gap: 20px;
}

.nav-links a {
  color: black;
  text-decoration: none;
  font-weight: 500;
  cursor: pointer;
}

.nav-links a:hover {
  text-decoration: none;
  font-weight: 600;
}
</style>