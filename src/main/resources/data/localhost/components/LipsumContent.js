export default {
    name: 'LipsumContent',
    props: {
        heading: {
            default: null,
            type: Object,
        },
        paragraph: {
            default: null,
            type: String,
        },
    },
    template: `
        <div>
            <component
                v-if="heading"
                :is="\`h\${heading.level}\`"
                class="font-semibold"
                :class="{
                    'text-4xl': \`\${heading.level}\` === '1',
                    'text-3xl': \`\${heading.level}\` === '2',
                    'text-2xl': \`\${heading.level}\` === '3',
                    'text-xl': \`\${heading.level}\` === '4',
                    'text-lg': \`\${heading.level}\` === '5',
                }"
            >
                {{ heading.text }}
            </component>
            <p
                v-if="paragraph"
                class="mt-4"
            >
                {{ paragraph }}
            </p>
        </div>
    `,
    schema: {
        heading: {
            label: 'Heading',
            type: 'fieldset',
            fields: {
                text: {
                    label: 'Text',
                    type: 'text',
                },
                level: {
                    label: 'Level',
                    type: 'select',
                    options: [1, 2, 3, 4, 5, 6],
                    default: 2,
                },
            },
        },
        paragraph: {
            label: 'Paragraph',
            type: 'textarea',
        },
    },
}
