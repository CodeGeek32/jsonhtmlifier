(function (doc) {
    let example = '{"loanRequestId":"555f9f35-3694-4daa-9cc4-d8cf8207f7f1","requestHistoryFrontId":null,"loanRequestExtId":"3566a2f6-18b1-4ba7-a9a9-a9275511a809","loanRequestExtNum":null,"customerRequestExtId":"2e94cb8e-5949-448e-82f1-e8c25cc114d4","extCreateDttm":{"year":2020,"month":"JULY","nano":0,"monthValue":7,"dayOfMonth":3,"hour":12,"minute":11,"second":0,"dayOfWeek":"FRIDAY","dayOfYear":185,"chronology":{"calendarType":"iso8601","id":"ISO"}},"callCd":1,"iterationNum":3,"statusCd":null,"channelCd":220,"loanRequestDttm":{"year":2020,"month":"AUGUST","nano":469000000,"monthValue":8,"dayOfMonth":11,"hour":14,"minute":15,"second":37,"dayOfWeek":"TUESDAY","dayOfYear":224,"chronology":{"calendarType":"iso8601","id":"ISO"}},"loanRequestUnixDttm":1597155337,"loanRequestUpdDttm":null,"onlinePaOffer":{"offerId":"661256f7-e95b-4244-bc0e-125f2bd28fcc","customerMdmId":null,"offerExpirationDt":{"year":2020,"month":"MAY","chronology":{"calendarType":"iso8601","id":"ISO"},"leapYear":true,"monthValue":5,"dayOfMonth":12,"dayOfWeek":"TUESDAY","era":"CE","dayOfYear":133},"creditLimit":162000.00,"cashInterestRateVal":11.00000,"posInterestRateVal":11.00000,"currencyCd":101018,"calRiskGradeCd":10460021,"productCd":10410001,"approvedCreditAmt":null,"creditPeriod":null,"contractId":null,"topupLoanAgreementNum":null,"bisSourceSystemNum":null,"sourceSystemCd":null},"creditParameters":[{"productKindCd":10260001,"productCd":null,"currencyCd":101010,"requestedSum":650000.00000,"creditPeriod":null,"insuranceAgrFlg":null}],"loanRequestComments":[{"commentOwnerLogin":"petr.zhukov","commentTypeCd":13,"commentCreateDttm":{"year":2020,"month":"JULY","nano":0,"monthValue":7,"dayOfMonth":3,"hour":12,"minute":11,"second":0,"dayOfWeek":"FRIDAY","dayOfYear":185,"chronology":{"calendarType":"iso8601","id":"ISO"}},"commentText":"На согласовании1"}],"employees":[{"lastNm":"Крылов","firstNm":"Павел","thirdNm":"Анатольевич","employeeNum":"0076560","employeeLoginNm":"petr.zhukov","bisDepartmentCd":99999,"bankDepartmentCd":10,"salePointAddress":"г. Тверь, ул. Вагжанова, 15","employeeRoleCd":10}],"participants":[{"customerFormId":"47611747-5c68-4bbb-9696-3fe2c4f3b130","participantRoleCd":10060001}],"dwhPaOffers":[],"creditIssuanceResults":[],"webAnalytics":[]}';
    example = JSON.parse(example);

    let b = doc.getElementById("root");

    // let example = { "number": "123", "lala": null, "obj" : { "propery one" : "some value", "property two" : 8888}, "some name": 123.312, "another name": 333 };
    // let example = { "number": 123, "arrya": [null, 2, "3", 1232, { "key": "value", "anotherKey" : 12321 }, "asdf"] };
    // let example = [[1], 2, 3, 4, 5];

    // let example = { "leapYear" : true };

    b.appendChild(jsonToHtml(example, doc));
    attachHandlers(b);

})(document);

function jsonToHtml(json, document) {

    if (tellType(json) == 'object') {
        let div = document.createElement('div');
        div.appendChild(objectOpeningBracket(document));

        div.appendChild(collapseButton(document));

        let obj = htmlifyObject(json, document);
        obj.className += "toCollapse";
        div.appendChild(obj);

        div.appendChild(objectClosingBracket(document));

        return div;
    }
    else if (tellType(json) == 'array') {
        let div = document.createElement('div');
        div.appendChild(arrayOpeningBracket(document));

        div.appendChild(collapseButton(document));

        let arr = htmlifyArray(json, document);
        arr.className += "toCollapse";
        div.appendChild(arr);

        div.appendChild(arrayClosingBracket(document));

        return div;
    }

    return null;
}

// <div>
// <span class="string key"> "key" : </span><span class ="string value"> value </span>
// </div>
function createStringNode(document, k, v, isLast) {

    let div = document.createElement('div');
    let key = document.createElement('span');
    key.className += "stringKey";

    let value = document.createElement('span');
    value.className += "string";

    key.textContent = '"' + k + '" : ';
    value.textContent = '"' + v + '"';

    div.appendChild(key);
    div.appendChild(value);

    if (!isLast) {
        let comma = document.createElement('span');
        comma.className += "comma";
        comma.textContent = ',';
        div.appendChild(comma);
    }

    return div;
}

function createIntegerNode(document, k, v, isLast) {

    let div = document.createElement('div');
    let key = document.createElement('span');
    key.className += "integerKey";

    let value = document.createElement('span');
    value.className += "integer";

    key.textContent = '"' + k + '" : ';
    value.textContent = v;

    div.appendChild(key);
    div.appendChild(value);

    if (!isLast) {
        let comma = document.createElement('span');
        comma.className += "comma";
        comma.textContent = ',';
        div.appendChild(comma);
    }

    return div;
}

function createBoolNode(document, k, v, isLast) {

    let div = document.createElement('div');
    let key = document.createElement('span');
    key.className += "booleanKey";

    let value = document.createElement('span');
    value.className += "boolean";

    key.textContent = '"' + k + '" : ';
    value.textContent = v;

    div.appendChild(key);
    div.appendChild(value);

    if (!isLast) {
        let comma = document.createElement('span');
        comma.className += "comma";
        comma.textContent = ',';
        div.appendChild(comma);
    }

    return div;
}

function createNullNode(document, k, v, isLast) {

    let div = document.createElement('div');
    let key = document.createElement('span');
    key.className += "nullKey";

    let value = document.createElement('span');
    value.className += "null";

    key.textContent = '"' + k + '" : ';
    value.textContent = 'null';

    div.appendChild(key);
    div.appendChild(value);

    if (!isLast) {
        let comma = document.createElement('span');
        comma.className += "comma";
        comma.textContent = ',';
        div.appendChild(comma);
    }

    return div;
}

function htmlifyObject(json, document) {

    let length = Object.keys(json).length
    // let arr = new Array(length);
    let retVal = document.createElement('div');
    let i = 0;
    for (var propt in json) {

        let isLast = i + 1 >= length;
        // { "key" : "some string" };
        if (tellType(propt) == 'string' && tellType(json[propt]) == 'string') {
            retVal.appendChild(createStringNode(document, propt, json[propt], isLast));
        }
        // { "key" : 123 };
        else if (tellType(propt) == 'string' && tellType(json[propt]) == 'integer') {
            retVal.appendChild(createIntegerNode(document, propt, json[propt], isLast));
        }
        else if (tellType(propt) == 'string' && tellType(json[propt]) == 'float') {
            retVal.appendChild(createIntegerNode(document, propt, json[propt], isLast));
        }
        // { "key" : boolean }
        else if (tellType(propt) == 'string' && tellType(json[propt]) == 'boolean') {
            retVal.appendChild(createBoolNode(document, propt, json[propt], isLast));
        }
        // { "key" : null };
        else if (tellType(propt) == 'string' && tellType(json[propt]) == 'null') {
            retVal.appendChild(createNullNode(document, propt, json[propt], isLast));
        }
        // { "key" : object };
        else if (tellType(propt) == 'string' && tellType(json[propt]) == 'object') {

            let div = document.createElement('div');

            let spanOpening = document.createElement('span');
            spanOpening.className += "objectKey";
            spanOpening.className += " collapsibleButton";
            spanOpening.textContent = '"' + propt + '" : {';
            div.appendChild(spanOpening);

            div.appendChild(collapseButton(document));

            let obj = htmlifyObject(json[propt], document);
            obj.className += "toCollapse";
            div.appendChild(obj);

            div.appendChild(objectClosingBracket(document));

            if (!isLast) {
                let comma = document.createElement('span');
                comma.className += "comma";
                comma.textContent = ',';
                div.appendChild(comma);
            }

            retVal.appendChild(div);
        }
        // { "key" : array };
        else if (tellType(propt) == 'string' && tellType(json[propt]) == 'array') {

            let div = document.createElement('div');

            let arrayHasElements = json[propt].length > 0;

            let spanOpening = document.createElement('span');
            spanOpening.className += "arrayKey";
            if (arrayHasElements)
                spanOpening.className += " collapsibleButton";
            spanOpening.textContent = '"' + propt + '" : [';
            div.appendChild(spanOpening);

            if (arrayHasElements) {
                div.appendChild(collapseButton(document));

                let aaargh = htmlifyArray(json[propt], document);
                aaargh.className += "toCollapse";
                div.appendChild(aaargh);
            }

            div.appendChild(arrayClosingBracket(document));
            if (!isLast) {
                let comma = document.createElement('span');
                comma.className += "comma";
                comma.textContent = ',';
                div.appendChild(comma);
            }

            retVal.appendChild(div);
        }

        i++;
    }

    return retVal;
}

function htmlifyArray(_array, document) {

    let retVal = document.createElement('div');
    let div, value;

    let i = 0;
    while (i < _array.length) {

        let notLast = i + 1 < _array.length;

        if (tellType(_array[i]) == 'string') {

            div = document.createElement('div');

            value = document.createElement('span');
            value.className += "string";
            value.textContent = '"' + _array[i] + '"';

            div.appendChild(value);

            if (notLast) {
                let comma = document.createElement('span');
                comma.className += "comma";
                comma.textContent = ',';
                div.appendChild(comma);
            }

            retVal.appendChild(div);
        }
        else if (tellType(_array[i]) == 'integer') {

            div = document.createElement('div');

            value = document.createElement('span');
            value.className += "integer";
            value.textContent = _array[i];

            div.appendChild(value);

            if (notLast) {
                let comma = document.createElement('span');
                comma.className += "comma";
                comma.textContent = ',';
                div.appendChild(comma);
            }

            retVal.appendChild(div);
        }
        else if (tellType(_array[i]) == 'float') {
            div = document.createElement('div');

            value = document.createElement('span');
            value.className += "float";
            value.textContent = v;

            div.appendChild(value);

            if (notLast) {
                let comma = document.createElement('span');
                comma.className += "comma";
                comma.textContent = ',';
                div.appendChild(comma);
            }

            retVal.appendChild(div);
        }
        else if (tellType(_array[i]) == 'null') {
            div = document.createElement('div');

            value = document.createElement('span');
            value.className += "null";
            value.textContent = 'null';

            div.appendChild(value);

            if (notLast) {
                let comma = document.createElement('span');
                comma.className += "comma";
                comma.textContent = ',';
                div.appendChild(comma);
            }

            retVal.appendChild(div);
        }
        else if (tellType(_array[i]) == 'object') {

            div = document.createElement('div');
            div.appendChild(objectOpeningBracket(document));

            div.appendChild(collapseButton(document));

            let obj = htmlifyObject(_array[i], document);
            obj.className += "toCollapse";

            div.appendChild(obj);

            div.appendChild(objectClosingBracket(document));

            if (notLast) {
                let comma = document.createElement('span');
                comma.className += "comma";
                comma.textContent = ',';
                div.appendChild(comma);
            }

            retVal.appendChild(div);
        }
        else if (tellType(_array[i]) == 'array') {
            div = document.createElement('div');

            div.appendChild(arrayOpeningBracket(document));

            let arrayHasElements = _array[i].length > 0;

            if (arrayHasElements) {
                div.appendChild(collapseButton(document));

                let uniqueVariableName = htmlifyArray(_array[i], document);
                uniqueVariableName.className += "toCollapse";

                div.appendChild(uniqueVariableName);
            }

            div.appendChild(arrayClosingBracket(document));

            if (notLast) {
                let comma = document.createElement('span');
                comma.className += "comma";
                comma.textContent = ',';
                div.appendChild(comma);
            }

            retVal.appendChild(div);
        }

        i++;
    }

    return retVal;
}

function objectOpeningBracket(document) {

    let retVal = document.createElement('span');
    retVal.className += "objectKey";
    retVal.textContent = '{';

    return retVal;
}

function objectClosingBracket(document) {

    let retVal = document.createElement('span');
    retVal.className += "objectKey";
    retVal.textContent = '}';

    return retVal;
}

function arrayOpeningBracket(document) {

    let retVal = document.createElement('span');
    retVal.className += "arrayKey";
    retVal.textContent = '[';

    return retVal;
}

function arrayClosingBracket(document) {

    let retVal = document.createElement('span');
    retVal.className += "arrayKey";
    retVal.textContent = ']';

    return retVal;
}

function collapseButton(document) {
    let c = document.createElement('button');
    c.className += " collapsibleButton";
    c.textContent = "-";
    return c;
}

function isFloat(mixedVar) {
    return +mixedVar === mixedVar && (!isFinite(mixedVar) || !!(mixedVar % 1))
}

function tellType(o) {
    if (Array.isArray(o)) return 'array';
    else if (Number.isInteger(o)) return 'integer';
    else if (isFloat(o)) return 'float';
    else if (typeof (o) == 'string') return 'string';
    else if (o == null) return 'null';
    else if (typeof (o) == 'boolean') return 'boolean';
    return 'object';
}