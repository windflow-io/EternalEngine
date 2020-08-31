import {loadScript} from './windflowUtils.mjs';

function loadMonacoEditor() {
  return new Promise(async (resolve) => {
    if (window.monaco) {
        resolve(window.monaco);
        return;
    }

    const require = await loadScript('/vendor/monacoEditor/loader.js', 'require');
    require.config({ paths: { 'vs': '/vendor/monacoEditor/vs' }});
    require(['vs/editor/editor.main'], () => resolve(window.monaco));
  });
}

export const CodeEditor = {
  name: 'CodeEditor',
  props: {
    options: {
        default: () => ({
            language: 'javascript',
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
        const monaco = await loadMonacoEditor();
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
