<template>
  <div id="app">
    <headerComponent v-if="isLogged"  @logout="onLogout"  />
    <div class="main-container">
      <router-view @login="onLogin" />
    </div>

  </div>
</template>

<script>
import headerComponent from "@/components/Header.vue"

export default {
  components: { headerComponent },
  data() {
    return {
      isLogged: !!localStorage.getItem("token") // pour le 1er chargement
    };
  },
  methods: {
onLogin() {
        this.isLogged = true; // après Login
    },
     onLogout() {
      this.isLogged = false; // après logout
    }
  }
}
</script>

<style>
:root {
  --header-height: 60px;
}
* {
  font-family: 'Montserrat', sans-serif;
}
body,
html {
  margin: 0;
  padding: 0;
  height: 100%;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: var(--header-height);
  z-index: 1000;
}
.main-container {
  position: relative;
  z-index: 1;
}
</style>