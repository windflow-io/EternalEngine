import { app } from '/app/app.mjs';
import {
    addUrlListener,
    removeNamespace,
    mapQueryString,
    pushUrl,
    loadEditor,
    componentService,
} from '/modules/windflowUtils.mjs'

export const FlowIcon = {
    name: 'FlowIcon',
    props: ['icon'],
    data() {
        return {
            viewBox: '0 0 0 0'
        }
    },
    template: `
        <svg
            xmlns="http://www.w3.org/2000/svg"
            :viewBox="viewBox"
            ref="svgTag"
            fill="currentColor"
            style="width:auto;height:1em;"
        />
    `,
    mounted() {
        fetch('/icons/solid/' + this.icon + '.svg').then(r => r.text()).then(d => {
            let svg = new DOMParser().parseFromString(d, "image/svg+xml").firstChild;
            this.viewBox = svg.getAttribute('viewBox');
            svg.childNodes.forEach(n => {
                this.$refs.svgTag.append(n);
            })
        })
    }
}

export const CodeEditor = {
  name: 'CodeEditor',
  model: {
    prop: 'value',
    event: 'update',
  },
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
  watch: {
    value() {
        if (this.value === this.editor.getValue()) return;

        this.editor.setValue(this.value);
    }
  },
  methods: {
    async init() {
        const monaco = await loadEditor();
        this.loading = false;
        // Wait until re-render so that the editor container has correct dimensions.
        await this.$nextTick();
        this.editor = monaco.editor.create(this.$refs.editor, {
            ...this.options,
            value: this.value,
        });
    },
  },
  template: `
    <div>
        <div v-if="loading">
            LOADING ...
        </div>
        <div v-else>
            <div
                style="width:800px;height:600px;max-width:100%;max-height:100%;"
                ref="editor"
                @input="$emit('update', this.editor.getValue())"
            />
        </div>
    </div>
  `,
}

export const FlowFormGroup = {
    name: 'FlowFormGroup',
    props: {
        label: {
            required: true,
            type: String,
        },
        name: {
            required: true,
            type: String,
        },
    },
    template: `
        <div>
            <label
                :for="name"
                class="block text-sm leading-5 font-medium text-gray-700"
            >
                {{ label }}
            </label>
            <div class="mt-1">
                <slot/>
            </div>
        </div>
    `,
}

export const FlowFormFieldText = {
    name: 'FlowFormFieldText',
    components: {
        FlowFormGroup,
    },
    props: {
        label: {
            required: true,
            type: String,
        },
        name: {
            required: true,
            type: String,
        },
        value: {
            default: '',
            type: [Number, String],
        },
    },
    template: `
        <flow-form-group
            :label="label"
            :name="name"
        >
            <input
                :id="name"
                :value="value"
                :name="name"
                class="border border-gray-400 block w-full p-2 sm:text-sm sm:leading-5 rounded-md shadow-sm"
                @input.stop="$emit('input', $event.target.value)"
            >
        </flow-form-group>
    `,
}

export const FlowFormFieldTextarea = {
    name: 'FlowFormFieldTextarea',
    components: {
        FlowFormGroup,
    },
    props: {
        label: {
            required: true,
            type: String,
        },
        name: {
            required: true,
            type: String,
        },
        value: {
            default: '',
            type: String,
        },
    },
    template: `
        <flow-form-group
            :label="label"
            :name="name"
        >
            <textarea
                :id="name"
                :value="value"
                :name="name"
                class="border border-gray-400 block w-full p-2 h-24 sm:text-sm sm:leading-5 rounded-md shadow-sm"
                @input.stop="$emit('input', $event.target.value)"
            />
        </flow-form-group>
    `,
}

export const FlowFormFieldSelect = {
    name: 'FlowFormFieldSelect',
    components: {
        FlowFormGroup,
    },
    props: {
        label: {
            required: true,
            type: String,
        },
        name: {
            required: true,
            type: String,
        },
        value: {
            default: '',
            type: [Number, String],
        },
        options: {
            required: true,
            type: Array,
        },
    },
    template: `
        <flow-form-group
            :label="label"
            :name="name"
        >
            <select
                :id="name"
                :value="value"
                :name="name"
                class="border border-gray-400 block w-full p-2 sm:text-sm sm:leading-5 rounded-md shadow-sm"
                @input.stop="$emit('input', $event.target.value)"
            >
                <option
                    v-for="option in options"
                    :key="option"
                    :value="option"
                >
                    {{ option }}
                </option>
            </select>
        </flow-form-group>
    `,
}

const fieldMap = {
    select: FlowFormFieldSelect,
    text: FlowFormFieldText,
    textarea: FlowFormFieldTextarea,
};

export const FlowFormFields = {
    name: 'FlowFormFields',
    props: {
        fields: {
            required: true,
            type: Object,
        },
        value: {
            required: true,
            type: Object,
        },
    },
    computed: {
        fieldNames() {
            return Object.keys(this.fields);
        }
    },
    created() {
        this.fieldMap = fieldMap;
    },
    methods: {
        update(fieldName, fieldValue) {
            const newValue = {
                ...this.value,
                [fieldName]: fieldValue,
            };
            this.$emit('input', newValue);
        },
    },
    template: `
        <div class="space-y-6">
            <component
                v-for="fieldName in fieldNames"
                :key="fieldName"
                :is="fieldMap[fields[fieldName].type]"
                v-bind="fields[fieldName]"
                :name="fieldName"
                :value="value[fieldName]"
                @input="update(fieldName, $event)"
            />
        </div>
    `,
};

export const FlowFormFieldFieldset = {
    name: 'FlowFormFieldFieldset',
    components: {
        FlowFormFields,
    },
    props: {
        fields: {
            required: true,
            type: Object,
        },
        label: {
            required: true,
            type: String,
        },
        value: {
            required: true,
            type: Object,
        },
    },
    template: `
        <div>
            <div>{{ label }}</div>
            <div class="mt-1 p-4 border border-gray-300">
                <flow-form-fields
                    :fields="fields"
                    :value="value"
                    @input="$emit('input', $event)"
                />
            </div>
        </div>
    `,
};

fieldMap.fieldset = FlowFormFieldFieldset;

export const FlowToolbarForm = {
    name: 'FlowToolbarForm',
    components: {
        FlowFormFields,
    },
    props: {
        schema: {
            required: true,
            type: Object,
        },
        value: {
            default: () => ({}),
            type: Object,
        },
    },
    template: `
        <flow-form-fields
            :fields="schema"
            :value="value"
            @input="$emit('input', $event)"
        />
    `,
};

export const FlowToolbar = {
    name: 'FlowToolbar',
    components: {
        CodeEditor,
        FlowIcon,
        FlowToolbarForm,
    },
    data() {
        return {
            code: '',
            mode: null,
            dragging: false,
            offsetX: 0,
            offsetY: 0,
            mouseMoveListener: null,
            mouseUpListener: null,
        }
    },
    computed: {
        componentId() {
            return this.$store.state.editComponent;
        },
        namespacedComponentName() {
            if (!this.componentId) return null;

            const allComponents = [];
            this.$store.state.pageAreas.forEach(section => section.components.forEach(component => allComponents.push(component)));

            return allComponents.find(component => component.id === this.componentId).name;
        },
        componentName() {
            if (!this.componentId) return null;

            return removeNamespace(this.namespacedComponentName);
        },
        component() {
            return app.component(this.componentName);
        },
        content: {
            get() {
                if (!this.$store.state.pageData.components) return '';

                return this.$store.state.pageData.components[this.componentId];
            },
            set(content) {
                this.$store.commit('setPageData', {
                    ...this.$store.state.pageData,
                    components: {
                        ...this.$store.state.pageData.components,
                        [this.componentId]: content,
                    },
                });
            },
        },
    },
    watch: {
        namespacedComponentName(name) {
            this.loadCode();
        },
    },
    methods: {
        doDrag(mouse) {
            if (!this.dragging) return;

            this.$el.style.top = (mouse.clientY - this.offsetY + window.pageYOffset) + 'px';
            this.$el.style.left = (mouse.clientX - this.offsetX + window.pageXOffset) + 'px';
        },
        startDrag(mouse) {
            if (this.dragging) return;

            this.offsetY = mouse.clientY - this.$el.getBoundingClientRect().top;
            this.offsetX = mouse.clientX - this.$el.getBoundingClientRect().left;
            this.dragging = true;
            this.mouseMoveListener = window.addEventListener('mousemove', this.doDrag);
            this.mouseUpListener = window.addEventListener('mouseup', this.endDrag);
        },
        endDrag() {
            this.dragging = false;
            window.removeEventListener('mousemove', this.doDrag);
            window.removeEventListener('mouseup', this.endDrag);
        },
        exitEditMode() {
            pushUrl(`${window.location.pathname}${window.location.search}`);
        },
        async loadCode() {
            const code = await componentService.load(this.namespacedComponentName);
            this.code = code;
        },
        update() {
            this.$store.dispatch('updateComponent', {
                code: this.code,
                content: this.content,
                id: this.componentId,
            });
        },
    },
    template: `
        <div
            class="absolute z-30 rounded-lg bg-white bg-opacity-75 border-2 border-gray-700 text-gray-800 text-md"
            style="left:calc(100% - 380px); top:50px"
        >
            <div class="flex h-16">
                <button
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-500 hover:text-gray-800 cursor-move"
                    title="drag"
                    aria-label="drag"
                    @mousedown="startDrag"
                >
                    <flow-icon icon="grip-vertical" class="text-md" />
                </button>
                <button
                    v-if="!componentId"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-500 hover:text-gray-800"
                    title="add component"
                    aria-label="add component"
                >
                    <flow-icon icon="plus" class="text-md" />
                </button>
                <button
                    v-if="componentId"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-500 hover:text-gray-800"
                    title="edit content"
                    aria-label="edit content"
                    @click="mode = 'edit-content'"
                >
                    <flow-icon icon="edit" class="text-md" />
                </button>
                <button
                    v-if="componentId"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-500 hover:text-gray-800"
                    title="edit code"
                    aria-label="edit code"
                    @click="mode = 'edit-code'"
                >
                    <flow-icon icon="code" class="text-md" />
                </button>
                <button
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-500 hover:text-gray-800"
                    title="rollback (coming soon)"
                    aria-label="rollback (coming soon)"
                >
                    <flow-icon icon="undo-alt" class="text-md" />
                </button>
                <button
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-500 hover:text-gray-800"
                    title="save"
                    aria-label="save"
                    @click="update"
                >
                    <flow-icon icon="save" class="text-md"/>
                </button>
                <button
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-gray-500 hover:text-gray-800"
                    title="exit edit mode"
                    aria-label="exit edit mode"
                    @click="exitEditMode"
                >
                    <flow-icon icon="times" class="text-md" />
                </button>
            </div>
            <div
                v-if="componentName"
                class="ml-4 text-xs"
            >
                {{ componentName }}
            </div>
            <transition name="fade">
                <div
                    v-if="mode"
                    class="absolute top w-full h-64"
                    style="top:100%;"
                >
                    <div
                        v-if="mode === 'edit-content' && component.schema"
                        class="p-6 bg-white"
                    >
                        <flow-toolbar-form
                            :schema="component.schema"
                            :value="content"
                            class="w-full h-full"
                            @input="content = $event"
                        />
                    </div>
                    <code-editor
                        v-if="mode === 'edit-code'"
                        :value="code"
                        class="w-full h-full"
                        @update="code = $event"
                    />
                </div>
            </transition>
        </div>
    `,
}

export const FlowApplication = {
    name: 'FlowApplication',
    components: {
        FlowToolbar,
    },
    template: `
        <div>
            <flow-toolbar v-if="$store.state.editMode"/>
            <component :is="removeNamespace(layoutComponent)" :key="currentPath"/>
        </div>
    `,
    data() {
        return {
            host:  mapQueryString(window.location.href).host || location.host,
            path: location.pathname,
            currentPath: location.pathname
        }
    },
    computed: {
        layoutComponent() {
            return this.$store.state.pageLayout;
        },
    },
    created() {
        addUrlListener(this.urlChanged);
    },
    beforeMount() {
        this.pageLoad(this.host, this.path);
    },
    methods: {
        urlChanged(current) {
            this.currentPath = current;
            this.pageLoad(this.host, current);
        },
        pageLoad(host, path) {
            this.$store.dispatch('fetchPageData', {host:host, path:path});
        },
        removeNamespace: removeNamespace
    },
}

export const FlowArea = {
    name: 'FlowArea',
    data() {
        return {
            host:window.location.host,
            path:window.location.pathname
        }
    },
    props: ['name'],
    computed: {
        areaComponents() {
            const area = this.$store.state.pageAreas.find(({ area }) => area === this.name);
            return area ? area.components : [];
        }
    },
    methods: {
        async addComponent() {
            const componentId = await this.$store.dispatch('addComponent', { area: this.name });
            this.editComponent(componentId);
        },
        editComponent(id) {
            if (!this.$store.state.editMode) return;

            this.$store.commit('setEditComponent', id);
        },
        getComponentData(id) {
            if (!this.$store.state.pageData.components) return null;

            return this.$store.state.pageData.components[id];
        },
        removeNamespace: removeNamespace
    },
    template: `
        <div>
            <component
                v-for="component in areaComponents"
                :key="component.id"
                :is="removeNamespace(component.name)"
                v-bind="getComponentData(component.id)"
                :style="$store.state.editMode && 'outline: rgba(0, 0, 0, 0.3) dashed 1px;'"
                :class="$store.state.editMode && 'transition-colors transition-opacity duration-200 bg-opacity-0 hover:bg-gray-100 hover:bg-opacity-25'"
                @click="editComponent(component.id)"
            />
            <div
                v-if="$store.state.editMode"
                style="outline: rgba(0, 0, 0, 0.3) dashed 1px;"
                class="flex p-8 justify-center items-center"
            >
                <button @click="addComponent">
                    Add new component
                </button>
            </div>
        </div>
    `,
}

export const FlowLink = {
    name: 'FlowLink',
    props: ['to'],
    template: '<a :href="to" @click="click($event)"><slot/></a>',
    methods: {
        click(event) {
            pushUrl(this.to)
            event.preventDefault();
        }
    }
}

export const ErrorLayout = {
    name: 'ErrorLayout',
    template:
        `<div class="flex flex-row h-screen items-center justify-center p-10 bg-gray-900">
            <div class="relative z-10 max-w-4xl opacity-100">
                <section class="flex flex-col">
                    <h1 class="flex text-6xl justify-center font-semibold text-gray-300">
                        {{ $store.state.pageData.errorTitle }}
                    </h1>
                    <h2 class="flex text-3xl justify-center text-gray-600">
                        {{ $store.state.pageData.errorDescription }}
                    </h2>
                    <h2 class="flex text-xl justify-center text-gray-700 mt-4">
                        {{ $store.state.pageData.errorDetail }}
                    </h2>
                </section>
            </div>
        </div>
         `,
}
