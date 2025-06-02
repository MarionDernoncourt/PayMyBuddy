<template>
  <div class="transfer-container">

    <p><strong>Solde :</strong> {{ balance }} €</p>

    <form @submit.prevent="sendMoney" class="transfer-form">
      <select id="friend" v-model="selectedFriendEmail" required>
        <option disabled value="">Sélectionner une relation</option>
        <option v-for="friend in friends" :key="friend.email" :value="friend.email">
          {{ friend.email }}
        </option>
      </select>

      <input id="description" v-model="description" placeholder="Description" required />

      <input id="amount" v-model.number="amount" type="number" min="0.01" step="0.01" placeholder="0€" />

      <button type="submit">Payer</button>
    </form>

    <p v-if="message" :class="{ error: isError }">{{ message }}</p>

    <h3>Historique des transactions</h3>
    <table v-if="transactions.length">
      <thead>
        <tr>
          <th>Date</th>
          <th>Vers</th>
          <th>Description</th>
          <th>Montant (€)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="txn in transactions" :key="txn.id">
          <td>{{ new Date(txn.date).toLocaleDateString() }}</td>
          <td>{{ txn.recipientEmail }}</td>
          <td>{{ txn.description }}</td>
          <td>{{ txn.amount.toFixed(2) }}</td>
        </tr>
      </tbody>
    </table>
    <p v-else>Aucune transaction pour l’instant.</p>
  </div>
</template>

<script>
import axios from 'axios';

// import axios from "axios";

export default {
  name: "transferPage",
  data() {
    return {
      balance: 0,
      friends: [],
      selectedFriendEmail: "",
      description: "",
      amount: null,
      message: "",
      isError: false,
      transactions: [],
    };
  },
  created() {
    this.fetchBalance();
    this.fetchFriends();
  },
  methods: {

    fetchBalance() {

      axios.get('http://localhost:8081/api/users/account',
         {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json'  // optionnel mais conseillé
            },
            withCredentials: true
          }
      )
        .then(res => {          
          console.log(res.data);
          this.balance = res.data;
        })
        .catch(() => {
          this.message = "Erreur lors de la récupération du solde";
          this.isError = true;
        });
    },
    fetchFriends() {
      axios.get('http://localhost:8081/api/users/getFriends',   {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json'  // optionnel mais conseillé
            },
            withCredentials: true
          })
        .then(res => {
          this.friends = res.data;
        })
        .catch(() => {
          this.message = "Erreur lors de la récupération des relations";
          this.isError = true;
        });
    },
    sendMoney() {
      this.message = "";
      this.isError = "false";

      if (!this.selectedFriendEmail) {
        this.message = "Veuillez sélectionner une relation";
        this.isError = true;
        return;
      }
      if (!this.amount || this.amount <= 0) {
        this.message = "Veuillez entrer un montant valide";
        this.isError = true;
        return;
      }
      if (!this.description.trim()) {
        this.message = "Veuillez saisir une description";
        this.isError = true;
        return;
      }

      const payload = {
        friendEmail: this.selectedFriendEmail,
        amount: this.amount,
        description: this.description,
      };

      axios.post('/api/transaction/transactions', payload)
        .then(() => {
          this.message = "Transfert effecuté avec succès";
          this.isError = false;

          this.fetchBalance();

          this.selectedFriendEmail = "";
          this.amount = null;
          this.description = "";
        })
        .catch(() => {
          this.message = "Erreur lors du transfert";
          this.isError = true;

        });
    },

  },
};
</script>