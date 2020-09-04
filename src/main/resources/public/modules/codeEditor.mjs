import {loadEditor} from './windflowUtils.mjs';

export const CodeEditor = {
  name: 'CodeEditor',
  props: {
    options: {
        default: () => ({
            language: 'html',
        }),
        type: Object,
    },
    value: {
        default: '',
        type: String,
    },
  },
  data() {
    return {
      loading: true,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    async init() {
        const monaco = await loadEditor();
        this.loading = false;
        // Wait until re-render so that the editor container has correct dimensions.
        await this.$nextTick();
        this.editor = monaco.editor.create(this.$refs.editor, this.options);
    },
    save() {
        /**@TODO: Logging for demo purposes only, remove later **/
        console.log(this.editor.getValue());
        this.$emit('input', this.editor.getValue());
    },
  },
  template: `
    <div>
        <div v-if="loading">
            LOADING ...
        </div>
        <div v-else>
            <button @click="save">
                Save
            </button>
            <div
                style="width:800px;height:600px"
                ref="editor"
            />
        </div>
    </div>
  `,
}
