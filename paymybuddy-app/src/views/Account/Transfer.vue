<template>
  <div class="transfer-container">
    <h2>Transfert d'argent</h2>

    <p><strong>Solde :</strong> {{ balance.toFixed(2) }} €</p>

    <form @submit.prevent="sendMoney" class="transfer-form">
      <label for="friend">Destinataire</label>
      <select id="friend" v-model="selectedFriendEmail" required>
        <option disabled value="">-- Choisir un ami --</option>
        <option v-for="friend in friends" :key="friend.email" :value="friend.email">
          {{ friend.email }}
        </option>
      </select>

      <label for="description">Description</label>
      <input id="description" v-model="description" placeholder="Ex: Remboursement resto" required />

      <label for="amount">Montant (€)</label>
      <input id="amount" v-model.number="amount" type="number" min="0.01" step="0.01" required />

      <button type="submit">Envoyer</button>
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

export default {
    name:"transferPage"
}
</script>