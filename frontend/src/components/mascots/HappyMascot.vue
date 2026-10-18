<template>
  <div class="mascot happy-mascot">
    <svg viewBox="0 0 190 190" preserveAspectRatio="xMidYMid meet">
      <!-- Head background -->
      <circle cx="95" cy="95" r="86" fill="#F6EBCD"/>
      <circle cx="95" cy="95" r="66" fill="#E5AC60"/>
      
      <!-- Left ear -->
      <circle cx="65" cy="63" r="16" fill="#E5AC60"/>
      <circle cx="65" cy="63" r="16" fill="#E5AC60"/>
      
      <!-- Right ear -->
      <circle cx="125" cy="63" r="16" fill="#E5AC60"/>
      
      <!-- Snout -->
      <circle cx="95" cy="85" r="49" fill="#FFE0A0"/>
      
      <!-- Left eye white -->
      <circle cx="75" cy="88" r="7" fill="#394A43"/>
      <circle cx="76" cy="89" r="2" fill="white"/>
      
      <!-- Right eye white -->
      <circle cx="110" cy="88" r="7" fill="#394A43"/>
      <circle cx="111" cy="89" r="2" fill="white"/>
      
      <!-- Left cheek -->
      <ellipse cx="61" cy="105" rx="7.5" ry="4" fill="#F5B0A0"/>
      
      <!-- Right cheek -->
      <ellipse cx="129" cy="105" rx="7.5" ry="4" fill="#F5B0A0"/>
      
      <!-- Mouth background -->
      <ellipse cx="95" cy="108" rx="19.5" ry="12" fill="#FFF0CE"/>
      
      <!-- Mouth line -->
      <path d="M 86 122 Q 95 127 104 122" stroke="#7E5E45" stroke-width="3" fill="none" stroke-linecap="round"/>
      
      <!-- Left mouth corner -->
      <ellipse cx="54" cy="145" rx="17.5" ry="10" fill="#F5CA7C"/>
      
      <!-- Right mouth corner -->
      <ellipse cx="136" cy="145" rx="17.5" ry="10" fill="#F5CA7C"/>
    </svg>
  </div>
</template>

<script setup>
</script>

<style scoped>
.mascot {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.happy-mascot svg {
  width: 100%;
  height: 100%;
}
</style>
