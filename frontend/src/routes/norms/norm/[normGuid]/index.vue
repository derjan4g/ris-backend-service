<script lang="ts" setup>
import { storeToRefs } from "pinia"
import { isDocumentSection, isArticle } from "@/domain/norm"
import { useLoadedNormStore } from "@/stores/loadedNorm"

const store = useLoadedNormStore()
const { loadedNorm } = storeToRefs(store)
</script>

<template>
  <div v-if="loadedNorm">
    <div class="max-w-screen-md">
      <h1 class="ds-heading-02-reg mb-44 text-center font-bold">
        {{
          loadedNorm.metadataSections?.NORM?.[0]?.OFFICIAL_LONG_TITLE?.[0] ?? ""
        }}
      </h1>
      <div v-for="doc in loadedNorm.documentation" :key="doc.guid">
        <h2
          class="mt-40 text-center"
          :class="[
            isDocumentSection(doc) &&
            doc.marker &&
            ['Eingangsformel', 'Schlussformel'].includes(doc.marker)
              ? 'ds-label-01-bold mb-16'
              : 'ds-label-01-reg',
          ]"
        >
          {{ doc.marker }}
        </h2>
        <h2 v-if="doc.heading" class="ds-label-01-bold mb-16 text-center">
          {{ doc.heading }}
        </h2>

        <template v-if="isDocumentSection(doc)">
          <div
            v-for="article in doc.documentation"
            :key="article.guid"
            class="mb-24"
          >
            <!-- eslint-disable vue/no-v-html -->
            <p
              v-if="isArticle(article)"
              v-html="
                article.marker === null
                  ? article.text
                  : article.marker + ' ' + article.text
              "
            />
            <!-- eslint-enable vue/no-v-html -->
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<!-- eslint-disable-next-line vue-scoped-css/enforce-style-type-->
<style>
dl {
  padding-left: 2rem;
}

p > dl {
  padding-top: 1rem;
}

dt {
  float: left;
}

dd {
  padding-left: 2rem;
}
</style>
