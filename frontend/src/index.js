import React from 'react';
import { render } from 'react-dom';

import App from './comp/App';
import { exampleRequest, jsonRequest } from "./api";

render(<App />, document.getElementById('root'));

window.ebiz = {
    exampleRequest,
    jsonRequest,
}
