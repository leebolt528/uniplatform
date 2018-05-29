/*!
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Mykhailo Stadnyk <mikhus@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * @version 2.0.8
 */
!function (e) {
    "use strict";
    function t(e) {
        if (Array.isArray(e)) {
            for (var t = 0, r = Array(e.length); t < e.length; t++)r[t] = e[t];
            return r
        }
        return Array.from(e)
    }

    function r(e, t) {
        if (!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
        return !t || "object" != typeof t && "function" != typeof t ? e : t
    }

    function i(e, t) {
        if ("function" != typeof t && null !== t)throw new TypeError("Super expression must either be null or a function, not " + typeof t);
        e.prototype = Object.create(t && t.prototype, {
            constructor: {
                value: e,
                enumerable: !1,
                writable: !0,
                configurable: !0
            }
        }), t && (Object.setPrototypeOf ? Object.setPrototypeOf(e, t) : e.__proto__ = t)
    }

    function o(e, t) {
        if (!(e instanceof t))throw new TypeError("Cannot call a class as a function")
    }

    function n(e, t) {
        if (t || (t = "undefined" == typeof window ? global : window), "undefined" != typeof t[e])return t[e];
        for (var r = ["webkit", "moz", "ms", "o"], i = 0, o = r.length, n = e.charAt(0).toUpperCase() + e.substr(1); i < o; i++) {
            var a = t[r[i] + n];
            if ("undefined" != typeof a)return a
        }
        return null
    }

    function a(e, t, r, i, o, n, l) {
        if ("function" != typeof i)throw new TypeError("Invalid animation rule:", i);
        var s = e - r, d = s / o;
        d > 1 && (d = 1), t && t(1 === d ? d : i(d)), s < o ? l.frame = oe(function (e) {
            return a(e, t, r, i, o, n, l)
        }) : n && n()
    }

    function l() {
        Array.prototype.constructor.apply(this, arguments)
    }

    function s(e) {
        if (!(e instanceof DOMException && 2152923147 === e.result))throw e
    }

    function d(e) {
        return e.majorTicks instanceof Array || (e.majorTicks = e.majorTicks ? [e.majorTicks] : []), e.majorTicks.length || (e.majorTicks.push(me.formatMajorTickNumber(e.minValue, e)), e.majorTicks.push(me.formatMajorTickNumber(e.maxValue, e))), ["right" !== e.tickSide, "left" !== e.tickSide]
    }

    function c(e, t, r, i, o, n) {
        e.beginPath(), e.moveTo(t + n, r), e.lineTo(t + i - n, r), e.quadraticCurveTo(t + i, r, t + i, r + n), e.lineTo(t + i, r + o - n), e.quadraticCurveTo(t + i, r + o, t + i - n, r + o), e.lineTo(t + n, r + o), e.quadraticCurveTo(t, r + o, t, r + o - n), e.lineTo(t, r + n), e.quadraticCurveTo(t, r, t + n, r), e.closePath()
    }

    function h(e, t) {
        var r = t.valueDec, i = t.valueInt, o = 0, n = void 0, a = void 0, l = void 0;
        if (e = parseFloat(e), l = e < 0, e = Math.abs(e), r > 0) {
            for (a = e.toFixed(r).toString().split("."), n = i - a[0].length; o < n; ++o)a[0] = "0" + a[0];
            a = (l ? "-" : "") + a[0] + "." + a[1]
        } else {
            for (a = Math.round(e).toString(), n = i - a.length; o < n; ++o)a = "0" + a;
            a = (l ? "-" : "") + a
        }
        return a
    }

    function u(e, t) {
        var r = void 0, i = !1;
        return r = 0 === t.majorTicksDec ? Math.round(e).toString() : e.toFixed(t.majorTicksDec), t.majorTicksInt > 1 ? (i = ~r.indexOf("."), ~r.indexOf("-") ? "-" + [t.majorTicksInt + t.majorTicksDec + 2 + (i ? 1 : 0) - r.length].join("0") + r.replace("-", "") : [t.majorTicksInt + t.majorTicksDec + 1 + (i ? 1 : 0) - r.length].join("0") + r) : r
    }

    function f(e) {
        return e * Math.PI / 180
    }

    function v(e, t) {
        return {x: -e * Math.sin(t), y: e * Math.cos(t)}
    }

    function m(e, t, r, i) {
        var o = arguments.length <= 4 || void 0 === arguments[4] || arguments[4],
            n = arguments.length <= 5 || void 0 === arguments[5] ? 0 : arguments[5],
            a = e.createLinearGradient(o ? 0 : n, o ? n : 0, o ? 0 : i, o ? i : 0);
        return a.addColorStop(0, t), a.addColorStop(1, r), a
    }

    function b(e, t) {
        var r = !(arguments.length <= 2 || void 0 === arguments[2]) && arguments[2];
        if (r)return e.restore(), !0;
        e.save();
        var i = t.borderShadowWidth;
        return i && (e.shadowBlur = i, e.shadowColor = t.colorBorderShadow), !0
    }

    function g(e, t) {
        t.needleShadow && (e.shadowOffsetX = 2, e.shadowOffsetY = 2, e.shadowBlur = 10, e.shadowColor = t.colorNeedleShadowDown)
    }

    function p(e, t, r, i, o, n) {
        if (t.valueBox) {
            var a = t.valueText || h(r, t);
            e.shadowOffsetX = null, e.shadowOffsetY = null, e.shadowBlur = null, e.shadowColor = "", e.strokeStyle = null, e.lineWidth = 0, e.save(), e.font = t.fontValueStyle + " " + t.fontValueWeight + " " + t.fontValueSize * (n / 200) + "px " + t.fontValue, e.save(), e.beginPath();
            var l = .12 * n, s = parseFloat(t.valueBoxStroke) || 0, d = 2 * n - 2 * s,
                u = e.measureText(t.valueText ? a : "-" + h(0, t)).width, f = u + .05 * n, v = l + .07 * n,
                m = n * t.valueBoxBorderRadius / 100, b = (parseFloat(t.valueBoxWidth) || 0) / 100 * d;
            b > f && (f = b), f > d && (f = d);
            var g = i - f / 2, p = o - l - .04 * n;
            m ? c(e, g, p, f, v, m) : e.rect(g, p, f, v);
            var w = o - .12 * n - .025 * n + (.12 * n + .045 * n) / 2;
            if (t.valueBoxStroke) {
                var y = e.createRadialGradient(i, w, n / 10, i, w, n / 5);
                y.addColorStop(0, t.colorValueBoxRect), y.addColorStop(1, t.colorValueBoxRectEnd), e.strokeStyle = y, e.lineWidth = n * t.valueBoxStroke / 100, e.stroke()
            }
            t.colorValueBoxShadow && (e.shadowBlur = .012 * n, e.shadowColor = t.colorValueBoxShadow), t.colorValueBoxBackground && (e.fillStyle = t.colorValueBoxBackground, e.fill()), e.closePath(), e.restore(), t.valueTextShadow && (e.shadowOffsetX = .004 * n, e.shadowOffsetY = .004 * n, e.shadowBlur = .012 * n, e.shadowColor = t.colorValueTextShadow), e.fillStyle = t.colorValueText, e.textAlign = "center", e.fillText(a, -i, o), e.restore()
        }
    }

    function w(e, t, r, i, o) {
        r.beginPath(), r.arc(0, 0, ue(e), 0, 2 * be, !0), r.lineWidth = t, r.strokeStyle = o ? me.linearGradient(r, i, o, e) : i, r.stroke(), r.closePath()
    }

    function y(e, t) {
        return e.maxRadius || (e.maxRadius = e.max - t.borderShadowWidth - t.borderOuterWidth - t.borderMiddleWidth - t.borderInnerWidth + (t.borderOuterWidth ? .5 : 0) + (t.borderMiddleWidth ? .5 : 0) + (t.borderInnerWidth ? .5 : 0)), e.maxRadius
    }

    function x(e, t) {
        var r = t.borderShadowWidth, i = e.max - r - t.borderOuterWidth / 2,
            o = i - t.borderOuterWidth / 2 - t.borderMiddleWidth / 2 + .5,
            n = o - t.borderMiddleWidth / 2 - t.borderInnerWidth / 2 + .5, a = y(e, t), l = void 0, s = !1;
        e.save(), t.borderOuterWidth && (s = me.drawShadow(e, t, s), w(i, t.borderOuterWidth, e, t.colorBorderOuter, t.colorBorderOuterEnd)), t.borderMiddleWidth && (s = me.drawShadow(e, t, s), w(o, t.borderMiddleWidth, e, t.colorBorderMiddle, t.colorBorderMiddleEnd)), t.borderInnerWidth && (s = me.drawShadow(e, t, s), w(n, t.borderInnerWidth, e, t.colorBorderInner, t.colorBorderInnerEnd)), me.drawShadow(e, t, s), e.beginPath(), e.arc(0, 0, ue(a), 0, 2 * be, !0), t.colorPlateEnd ? (l = e.createRadialGradient(0, 0, a / 2, 0, 0, a), l.addColorStop(0, t.colorPlate), l.addColorStop(1, t.colorPlateEnd)) : l = t.colorPlate, e.fillStyle = l, e.fill(), e.closePath(), e.restore()
    }

    function T(e, t) {
        var r = e.max * (parseFloat(t.highlightsWidth) || 0) / 100;
        if (r) {
            var i = ue(S(e, t) - r / 2), o = 0, n = t.highlights.length, a = (t.maxValue - t.minValue) / t.ticksAngle;
            for (e.save(); o < n; o++) {
                var l = t.highlights[o];
                e.beginPath(), e.rotate(ge), e.arc(0, 0, i, me.radians(t.startAngle + (l.from - t.minValue) / a), me.radians(t.startAngle + (l.to - t.minValue) / a), !1), e.strokeStyle = l.color, e.lineWidth = r, e.stroke(), e.closePath(), e.restore(), e.save()
            }
        }
    }

    function k(e, t) {
        var r = S(e, t);
        e.lineWidth = se.pixelRatio, e.strokeStyle = t.colorMinorTicks, e.save();
        for (var i = t.minorTicks * (t.majorTicks.length - 1), o = 0; o < i; ++o) {
            var n = t.startAngle + o * (t.ticksAngle / i);
            e.rotate(me.radians(n)), e.beginPath(), e.moveTo(0, r), e.lineTo(0, r - .075 * e.max), M(e)
        }
    }

    function S(e, t) {
        return y(e, t) - .05 * e.max
    }

    function W(e, t) {
        me.prepareTicks(t);
        var r = ue(S(e, t)), i = void 0, o = void 0, n = t.majorTicks.length, a = se.pixelRatio;
        for (e.lineWidth = 2 * a, e.save(), o = t.colorMajorTicks instanceof Array ? t.colorMajorTicks : Array(n).fill(t.colorMajorTicks), i = 0; i < n; ++i)e.strokeStyle = o[i], e.rotate(me.radians(O(t, i, n))), e.beginPath(), e.moveTo(0, r), e.lineTo(0, r - .15 * e.max), M(e);
        t.strokeTicks && (e.strokeStyle = o[0], e.rotate(ge), e.beginPath(), e.arc(0, 0, r, me.radians(t.startAngle), me.radians(t.startAngle + t.ticksAngle), !1), M(e))
    }

    function O(e, t, r) {
        return e.startAngle + t * (e.ticksAngle / (r - 1))
    }

    function M(e) {
        e.stroke(), e.restore(), e.closePath(), e.save()
    }

    function B(e, t) {
        var r = y(e, t) - .35 * e.max, i = {}, o = 0, n = t.majorTicks.length, a = "needle" !== t.animationTarget,
            l = t.colorNumbers instanceof Array ? t.colorNumbers : Array(n).fill(t.colorNumbers),
            s = a ? -(t.value - t.minValue) / (t.maxValue - t.minValue) * t.ticksAngle : 0;
        for (a && (e.save(), e.rotate(-me.radians(s))); o < n; ++o) {
            var d = s + O(t, o, n), c = me.radialPoint(r, me.radians(d));
            360 === d && (d = 0), i[d] || (i[d] = !0, e.font = t.fontNumbersStyle + " " + t.fontNumbersWeight + " " + t.fontNumbersSize * (e.max / 200) + "px " + t.fontNumbers, e.fillStyle = l[o], e.lineWidth = 0, e.textAlign = "center", e.fillText(t.majorTicks[o], c.x, c.y + 3))
        }
        a && e.restore()
    }

    function P(e, t) {
        t.title && (e.save(), e.font = t.fontTitleStyle + " " + t.fontTitleWeight + " " + t.fontTitleSize * (e.max / 200) + "px " + t.fontTitle, e.fillStyle = t.colorTitle, e.textAlign = "center", e.fillText(t.title, 0, -e.max / 4.25, .8 * e.max), e.restore())
    }

    function V(e, t) {
        t.units && (e.save(), e.font = t.fontUnitsStyle + " " + t.fontUnitsWeight + t.fontUnitsSize * (e.max / 200) + "px " + t.fontUnits, e.fillStyle = t.colorUnits, e.textAlign = "center", e.fillText(t.units, 0, e.max / 3.25, .8 * e.max), e.restore())
    }

    function A(e, t) {
        if (t.needle) {
            var r = t.value, i = y(e, t), o = ue(i / 100 * t.needleCircleSize),
                n = ue(i / 100 * t.needleCircleSize * .75), a = ue(i / 100 * t.needleEnd),
                l = ue(t.needleStart ? i / 100 * t.needleStart : 0), s = ue(.2 * i), d = i / 100 * t.needleWidth,
                c = i / 100 * t.needleWidth / 2, h = se.pixelRatio, u = "needle" !== t.animationTarget;
            e.save(), me.drawNeedleShadow(e, t), e.rotate(me.radians(u ? t.startAngle : t.startAngle + (r - t.minValue) / (t.maxValue - t.minValue) * t.ticksAngle)), e.fillStyle = me.linearGradient(e, t.colorNeedle, t.colorNeedleEnd, a - s), "arrow" === t.needleType ? (e.beginPath(), e.moveTo(-c, -s), e.lineTo(-d, 0), e.lineTo(-1 * h, a), e.lineTo(h, a), e.lineTo(d, 0), e.lineTo(c, -s), e.closePath(), e.fill(), e.beginPath(), e.lineTo(-.5 * h, a), e.lineTo(-1 * h, a), e.lineTo(-d, 0), e.lineTo(-c, -s), e.lineTo(c / 2 * h - 2 * h, -s), e.closePath(), e.fillStyle = t.colorNeedleShadowUp, e.fill()) : (e.beginPath(), e.moveTo(-c, a), e.lineTo(-c, l), e.lineTo(c, l), e.lineTo(c, a), e.closePath(), e.fill()), t.needleCircleSize && (e.restore(), me.drawNeedleShadow(e, t), t.needleCircleOuter && (e.beginPath(), e.arc(0, 0, o, 0, 2 * be, !0), e.fillStyle = me.linearGradient(e, t.colorNeedleCircleOuter, t.colorNeedleCircleOuterEnd, o), e.fill(), e.closePath()), t.needleCircleInner && (e.beginPath(), e.arc(0, 0, n, 0, 2 * be, !0), e.fillStyle = me.linearGradient(e, t.colorNeedleCircleInner, t.colorNeedleCircleInnerEnd, n), e.fill(), e.closePath()), e.restore())
        }
    }

    function N(e, t, r) {
        me.drawValueBox(e, t, r, 0, e.max - .33 * e.max, e.max)
    }

    function C(e, t, r, i, o, n, a, l) {
        e.beginPath(), e.fillStyle = l ? me.linearGradient(e, a, l, o > n ? o : n, n > o, o > n ? r : i) : a, t > 0 ? me.roundRect(e, r, i, o, n, t) : e.rect(r, i, o, n), e.fill(), e.closePath()
    }

    function j(e, t, r, i, o, n, a, l, s) {
        e.beginPath(), e.lineWidth = t, e.strokeStyle = s ? me.linearGradient(e, l, s, a, !0, o) : l, r > 0 ? me.roundRect(e, i, o, n, a, r) : e.rect(i, o, n, a), e.stroke(), e.closePath()
    }

    function E(e, t, r, i, o, n) {
        e.save();
        var a = t.borderRadius, l = o - t.borderShadowWidth - t.borderOuterWidth,
            s = l - t.borderOuterWidth - t.borderMiddleWidth, d = s - t.borderMiddleWidth - t.borderInnerWidth,
            c = d - t.borderInnerWidth, h = n - t.borderShadowWidth - t.borderOuterWidth,
            u = h - t.borderOuterWidth - t.borderMiddleWidth, f = u - t.borderMiddleWidth - t.borderInnerWidth,
            v = f - t.borderInnerWidth, m = r - (s - l) / 2, b = m - (d - s) / 2, g = b - (c - d) / 2,
            p = i - (u - h) / 2, w = p - (f - u) / 2, y = w - (v - f) / 2, x = 0, T = !1;
        return t.borderOuterWidth && (T = me.drawShadow(e, t, T), j(e, t.borderOuterWidth, a, r + t.borderOuterWidth / 2 - x, i + t.borderOuterWidth / 2 - x, l, h, t.colorBorderOuter, t.colorBorderOuterEnd), x += .5), t.borderMiddleWidth && (T = me.drawShadow(e, t, T), j(e, t.borderMiddleWidth, a -= 1 + 2 * x, m + t.borderMiddleWidth / 2 - x, p + t.borderMiddleWidth / 2 - x, s + 2 * x, u + 2 * x, t.colorBorderMiddle, t.colorBorderMiddleEnd), x += .5), t.borderInnerWidth && (T = me.drawShadow(e, t, T), j(e, t.borderInnerWidth, a -= 1 + 2 * x, b + t.borderInnerWidth / 2 - x, w + t.borderInnerWidth / 2 - x, d + 2 * x, f + 2 * x, t.colorBorderInner, t.colorBorderInnerEnd), x += .5), me.drawShadow(e, t, T), C(e, a, g, y, c + 2 * x, v + 2 * x, t.colorPlate, t.colorPlateEnd), e.restore(), [g, y, c, v]
    }

    function R(e, t, r, i, o, n) {
        var a = se.pixelRatio, l = n >= o, s = l ? .85 * o : n, d = l ? n : o;
        r = l ? he(r + (o - s) / 2) : r;
        var c = !!t.title, h = !!t.units, u = !!t.valueBox, f = void 0, v = void 0, m = void 0;
        l ? (v = he(.05 * d), f = he(.075 * d), m = he(.075 * d), c && (d -= f, i += f), h && (d -= v), u && (d -= m)) : (v = f = he(.15 * s), c && (s -= f, i += f), h && (s -= v));
        var b = 2 * t.barStrokeWidth, g = t.barBeginCircle ? he(s * t.barBeginCircle / 200 - b / 2) : 0,
            p = he(s * t.barWidth / 100 - b), w = he(d * t.barLength / 100 - b), y = he((d - w) / 2),
            x = he(r + (l ? s / 2 : y + g)), T = he(i + (l ? d - y - g + b / 2 : s / 2)),
            k = !l || t.hasLeft && t.hasRight ? 0 : (t.hasRight ? -1 : 1) * t.ticksWidth / 100 * s,
            S = l || t.hasLeft && t.hasRight ? 0 : (t.hasRight ? -1 : 1) * t.ticksWidth / 100 * s;
        return e.barDimensions = {
            isVertical: l,
            width: s,
            length: d,
            barWidth: p,
            barLength: w,
            strokeWidth: b,
            barMargin: y,
            radius: g,
            pixelRatio: a,
            barOffset: null,
            titleMargin: c ? f : 0,
            unitsMargin: h ? v : 0,
            get ticksLength() {
                return this.barLength - this.barOffset - this.strokeWidth
            },
            X: r + k,
            Y: i + S,
            x0: x + k,
            y0: T + S,
            baseX: r,
            baseY: i,
            ticksPadding: t.ticksPadding / 100
        }, e.barDimensions
    }

    function I(e, t, r, i, o, n, a) {
        var l = R(e, t, i, o, n, a), s = l.isVertical, d = l.width, c = l.barWidth, h = l.barLength, u = l.strokeWidth,
            f = l.barMargin, v = l.radius, m = l.x0, b = l.y0, g = l.X, p = l.Y, w = h;
        if (e.save(), e.beginPath(), t.barBeginCircle) {
            var y = me.radians(s ? 270 : 0), x = Math.asin(c / 2 / v), T = Math.cos(x), k = Math.sin(x),
                S = m + (s ? v * k : v * T - u / 2), W = s ? b - v * T : b + v * k, O = ue(s ? W - b : S - m);
            e.barDimensions.barOffset = he(O + v);
            var M = s ? he(m - v * k) : S, B = s ? W : he(b - v * k);
            "progress" === r && (h = e.barDimensions.barOffset + (h - e.barDimensions.barOffset) * (t.value - t.minValue) / (t.maxValue - t.minValue));
            var P = he(S + h - e.barDimensions.barOffset + u / 2), V = he(W - h + e.barDimensions.barOffset - u / 2);
            e.arc(m, b, v, y + x, y - x), s ? (e.moveTo(S, B), e.lineTo(S, V), e.lineTo(M, V), e.lineTo(M, B)) : (e.moveTo(S, B), e.lineTo(P, B), e.lineTo(P, W), e.lineTo(S, W))
        } else {
            var A = he(s ? g + (d - c) / 2 : g + f), N = he(s ? p + h + f : p + (d - c) / 2);
            "progress" === r && (h *= (t.value - t.minValue) / (t.maxValue - t.minValue)), s ? e.rect(A, N, c, -h) : e.rect(A, N, h, c)
        }
        "progress" !== r && t.barStrokeWidth && (e.lineWidth = u, e.strokeStyle = t.colorBarStroke, e.stroke()), "progress" !== r && t.colorBar ? (e.fillStyle = t.colorBarEnd ? me.linearGradient(e, t.colorBar, t.colorBarEnd, h, s, s ? p : g) : t.colorBar, e.fill()) : "progress" === r && t.colorBarProgress && (e.fillStyle = t.colorBarProgressEnd ? me.linearGradient(e, t.colorBarProgress, t.colorBarProgressEnd, w, s, s ? p : g) : t.colorBarProgress, e.fill()), e.closePath(), t.barBeginCircle && (e.barDimensions.radius += u), e.barDimensions.barWidth += u, e.barDimensions.barLength += u
    }

    function D(e, t, r, i, o, n) {
        I(e, t, "", r, i, o, n)
    }

    function z(e, t) {
        return t.needleSide !== e || t.tickSide !== e || t.numberSide !== e
    }

    function L(e, t, r, i, o, n) {
        t.barProgress && I(e, t, "progress", r, i, o, n)
    }

    function _(e, t) {
        var r = e.barDimensions, i = r.isVertical, o = r.width, n = r.length, a = r.barWidth, l = r.barOffset,
            s = r.barMargin, d = r.X, c = r.Y, h = r.ticksLength, u = r.ticksPadding,
            f = o * (parseFloat(t.highlightsWidth) || 0) / 100;
        if (t.highlights && f) {
            var v = "right" !== t.tickSide, m = "left" !== t.tickSide, b = 0, g = t.highlights.length, p = (o - a) / 2,
                w = t.maxValue - t.minValue, y = he(i ? d + p : d + s + l), x = f, T = i ? c + n - s - l : c + p,
                k = he((t.ticksWidth / 100 + u) * o) + (f - t.ticksWidth / 100 * o), S = he(a + u * o);
            for (e.save(); b < g; b++) {
                var W = t.highlights[b], O = h * ue(W.from) / w, M = h * ue((W.to - W.from) / w);
                e.beginPath(), e.fillStyle = W.color, i ? (v && e.rect(y - k, T - O, x, -M), m && e.rect(y + S, T - O, x, -M)) : (v && e.rect(y + O, T - k, M, x), m && e.rect(y + O, T + S, M, x)), e.fill(), e.closePath()
            }
        }
    }

    function G(e, t, r, i, o) {
        e.beginPath(), e.moveTo(t, r), e.lineTo(i, o), e.stroke(), e.closePath(), e.save()
    }

    function X(e, t, r, i, o, n, a, l) {
        var s = e.barDimensions, d = s.isVertical, c = s.length, h = s.barWidth, u = s.barOffset, f = s.barMargin,
            v = s.pixelRatio, m = s.width, b = s.X, g = s.Y, p = s.ticksLength, w = s.ticksPadding, y = (m - h) / 2,
            x = void 0, T = void 0, k = 0, S = l * m, W = y - w * m, O = y + h + S + w * m, M = p / (r - i),
            B = t instanceof Array ? t : Array(r).fill(t);
        for (e.lineWidth = a * v, e.save(); k < r; k++)e.strokeStyle = B[k], d ? (T = g + c - f - u - k * M, o && (x = b + W, G(e, x, T, he(x - S), T)), n && (x = b + O, G(e, x, T, he(x - S), T))) : (x = b + f + u + k * M, o && (T = g + W, G(e, x, T, x, he(T - S))), n && (T = g + O, G(e, x, he(T), x, T - S)))
    }

    function Y(e, t) {
        var r = me.prepareTicks(t), i = te(r, 2), o = i[0], n = i[1], a = 2,
            l = t.colorMajorTicks instanceof Array ? t.colorMajorTicks : Array(t.colorMajorTicks.length).fill(t.colorMajorTicks);
        if (X(e, t.colorMajorTicks, t.majorTicks.length, 1, o, n, a, t.ticksWidth / 100), t.strokeTicks) {
            var s = e.barDimensions, d = s.isVertical, c = s.length, h = s.width, u = s.barWidth, f = s.barMargin,
                v = s.barOffset, m = s.X, b = s.Y, g = s.ticksLength, p = s.pixelRatio, w = s.ticksPadding,
                y = (h - u) / 2 + u + w * h, x = (h - u) / 2 - w * h, T = void 0, k = void 0, S = void 0, W = void 0;
            e.strokeStyle = l[0], a *= p, d ? (k = b + c - f - v + a / 2, W = k - g - a, o && (S = T = he(m + x), U(e, T, k, S, W)), n && (S = T = he(m + y), U(e, T, k, S, W))) : (T = m + f + v - a / 2, S = T + g + a, o && (W = k = he(b + x), U(e, T, k, S, W)), n && (W = k = he(b + y), U(e, T, k, S, W)))
        }
    }

    function U(e, t, r, i, o) {
        e.beginPath(), e.moveTo(t, r), e.lineTo(i, o), e.stroke(), e.closePath()
    }

    function F(e, t) {
        var r = me.prepareTicks(t), i = te(r, 2), o = i[0], n = i[1];
        X(e, t.colorMinorTicks, t.minorTicks * (t.majorTicks.length - 1), 0, o, n, 1, t.ticksWidthMinor / 100)
    }

    function q(e, t) {
        var r = e.barDimensions, i = r.isVertical, o = r.length, n = r.width, a = r.barWidth, l = r.barMargin,
            s = r.barOffset, d = r.X, c = r.Y, h = r.ticksLength, u = r.ticksPadding, f = t.majorTicks.length,
            v = "right" !== t.numberSide, m = "left" !== t.numberSide, b = t.fontNumbersSize * n / 200, g = 0,
            p = (t.ticksWidth / 100 + 2 * u) * n, w = (n - a) / 2 - p, y = (n - a) / 2 + a + p, x = void 0, T = void 0,
            k = void 0, S = void 0, W = void 0,
            O = t.colorNumbers instanceof Array ? t.colorNumbers : Array(f).fill(t.colorNumbers);
        for (e.font = t.fontNumbersStyle + " " + t.fontNumbersWeight + " " + b + "px " + t.fontNumbers, e.lineWidth = 0, e.textAlign = "center"; g < f; g++)e.fillStyle = O[g], W = t.majorTicks[g], S = g * h / (f - 1), i ? (T = c + o - l - s - S + b / 3, v && (e.textAlign = "right", e.fillText(W, d + w, T)), m && (e.textAlign = "left", e.fillText(W, d + y, T))) : (k = e.measureText(W).width, x = d + l + s + S, v && e.fillText(W, x, c + w), m && e.fillText(W, x, c + y + b))
    }

    function H(e, t) {
        if (t.title) {
            var r = e.barDimensions, i = r.isVertical, o = r.width, n = r.length, a = r.baseX, l = r.baseY,
                s = r.titleMargin, d = t.fontTitleSize * o / 200, c = he(a + (i ? o : n) / 2),
                h = he(l + s / 2 - (i ? d : d / 2) - .025 * (i ? n : o));
            e.save(), e.textAlign = "center", e.fillStyle = t.colorTitle, e.font = t.fontTitleStyle + " " + t.fontTitleWeight + " " + d + "px " + t.fontTitle, e.lineWidth = 0, e.fillText(t.title, c, h, i ? o : n)
        }
    }

    function J(e, t) {
        if (t.units) {
            var r = e.barDimensions, i = r.isVertical, o = r.width, n = r.length, a = r.baseX, l = r.baseY,
                s = r.unitsMargin, d = t.fontUnitsSize * o / 200, c = he(a + (i ? o : n) / 2),
                h = he(l + (i ? n : o) + s / 2 - d / 2);
            e.save(), e.textAlign = "center", e.fillStyle = t.colorTitle, e.font = t.fontUnitsStyle + " " + t.fontUnitsWeight + " " + d + "px " + t.fontUnits, e.lineWidth = 0, e.fillText(t.units, c, h, i ? o : n)
        }
    }

    function Z(e, t) {
        if (t.needle) {
            var r = e.barDimensions, i = r.isVertical, o = r.width, n = r.length, a = r.barWidth, l = r.barOffset,
                s = r.barMargin, d = r.ticksLength, c = r.X, h = r.Y, u = r.ticksPadding, f = "right" !== t.needleSide,
                v = "left" !== t.needleSide, m = d * (t.value - t.minValue) / (t.maxValue - t.minValue),
                b = (t.ticksWidth / 100 + u) * o, g = a / 2 + b, p = g * (t.needleEnd / 100), w = void 0, y = void 0,
                x = void 0, T = void 0, k = "arrow" === t.needleType.toLowerCase() ? Q : K, S = (o - a) / 2,
                W = g * (t.needleStart / 100), O = S - b - W, M = S + a + b + W;
            e.save(), me.drawNeedleShadow(e, t), i ? (x = he(h + n - s - l - m), f && (w = he(c + O), y = w + p, k(e, t, w, x, y, x, p)), v && (w = he(c + M), y = w - p, k(e, t, w, x, y, x, p, !0))) : (w = he(c + s + l + m), f && (x = he(h + O), T = x + p, k(e, t, w, x, w, T, p)), v && (x = he(h + M), T = x - p, k(e, t, w, x, w, T, p, !0))), e.restore()
        }
    }

    function $(e, t, r, i) {
        return t.colorNeedleEnd ? me.linearGradient(e, i ? t.colorNeedleEnd : t.colorNeedle, i ? t.colorNeedle : t.colorNeedleEnd, r, !e.barDimensions.isVertical) : t.colorNeedle
    }

    function K(e, t, r, i, o, n, a, l) {
        e.lineWidth = t.needleWidth, e.strokeStyle = $(e, t, a, l), e.beginPath(), e.moveTo(r, i), e.lineTo(o, n), e.stroke(), e.closePath()
    }

    function Q(e, t, r, i, o, n, a, l) {
        var s = he(.4 * a), d = a - s, c = r === o, h = t.needleWidth / 2;
        e.fillStyle = $(e, t, a, l), e.beginPath(), c ? (i > n && (d *= -1), e.moveTo(r - h, i), e.lineTo(r + h, i), e.lineTo(r + h, i + d), e.lineTo(r, n), e.lineTo(r - h, i + d), e.lineTo(r - h, i)) : (r > o && (d *= -1), e.moveTo(r, i - h), e.lineTo(r, i + h), e.lineTo(r + d, i + h), e.lineTo(o, i), e.lineTo(r + d, i - h), e.lineTo(r, i - h)), e.fill(), e.closePath()
    }

    function ee(e, t, r, i, o, n, a) {
        e.barDimensions.isVertical && me.drawValueBox(e, t, r, i + n / 2, o + a - 40 * (n / 300), n)
    }

    var te = function () {
        function e(e, t) {
            var r = [], i = !0, o = !1, n = void 0;
            try {
                for (var a, l = e[Symbol.iterator](); !(i = (a = l.next()).done) && (r.push(a.value), !t || r.length !== t); i = !0);
            } catch (e) {
                o = !0, n = e
            } finally {
                try {
                    !i && l.return && l.return()
                } finally {
                    if (o)throw n
                }
            }
            return r
        }

        return function (t, r) {
            if (Array.isArray(t))return t;
            if (Symbol.iterator in Object(t))return e(t, r);
            throw new TypeError("Invalid attempt to destructure non-iterable instance")
        }
    }(), re = function e(t, r, i) {
        null === t && (t = Function.prototype);
        var o = Object.getOwnPropertyDescriptor(t, r);
        if (void 0 === o) {
            var n = Object.getPrototypeOf(t);
            return null === n ? void 0 : e(n, r, i)
        }
        if ("value" in o)return o.value;
        var a = o.get;
        if (void 0 !== a)return a.call(i)
    }, ie = function () {
        function e(e, t) {
            for (var r = 0; r < t.length; r++) {
                var i = t[r];
                i.enumerable = i.enumerable || !1, i.configurable = !0, "value" in i && (i.writable = !0), Object.defineProperty(e, i.key, i)
            }
        }

        return function (t, r, i) {
            return r && e(t.prototype, r), i && e(t, i), t
        }
    }();
    Object.assign || Object.defineProperty(Object, "assign", {
        enumerable: !1,
        configurable: !0,
        writable: !0,
        value: function (e, t) {
            if (void 0 === e || null === e)throw new TypeError("Cannot convert first argument to object");
            for (var r = Object(e), i = 1; i < arguments.length; i++) {
                var o = arguments[i];
                if (void 0 !== o && null !== o)for (var n = Object.keys(Object(o)), a = 0, l = n.length; a < l; a++) {
                    var s = n[a], d = Object.getOwnPropertyDescriptor(o, s);
                    void 0 !== d && d.enumerable && (r[s] = o[s])
                }
            }
            return r
        }
    }), Array.prototype.indexOf || (Array.prototype.indexOf = function (e, t) {
        var r;
        if (null == this)throw new TypeError('"this" is null or not defined');
        var i = Object(this), o = i.length >>> 0;
        if (0 === o)return -1;
        var n = +t || 0;
        if (Math.abs(n) === 1 / 0 && (n = 0), n >= o)return -1;
        for (r = Math.max(n >= 0 ? n : o - Math.abs(n), 0); r < o;) {
            if (r in i && i[r] === e)return r;
            r++
        }
        return -1
    }), Array.prototype.fill || (Array.prototype.fill = function (e) {
        if (null == this)throw new TypeError("this is null or not defined");
        for (var t = Object(this), r = t.length >>> 0, i = arguments[1], o = i >> 0, n = o < 0 ? Math.max(r + o, 0) : Math.min(o, r), a = arguments[2], l = void 0 === a ? r : a >> 0, s = l < 0 ? Math.max(r + l, 0) : Math.min(l, r); n < s;)t[n] = e, n++;
        return t
    });
    var oe = n("requestAnimationFrame") || function (e) {
            return setTimeout(function () {
                return e((new Date).getTime())
            }, 1e3 / 60)
        }, ne = {
        linear: function (e) {
            return e
        }, quad: function (e) {
            return Math.pow(e, 2)
        }, dequad: function (e) {
            return 1 - ne.quad(1 - e)
        }, quint: function (e) {
            return Math.pow(e, 5)
        }, dequint: function (e) {
            return 1 - Math.pow(1 - e, 5)
        }, cycle: function (e) {
            return 1 - Math.sin(Math.acos(e))
        }, decycle: function (e) {
            return Math.sin(Math.acos(1 - e))
        }, bounce: function (e) {
            return 1 - ne.debounce(1 - e)
        }, debounce: function (e) {
            for (var t = 0, r = 1; 1; t += r, r /= 2)if (e >= (7 - 4 * t) / 11)return -Math.pow((11 - 6 * t - 11 * e) / 4, 2) + Math.pow(r, 2)
        }, elastic: function (e) {
            return 1 - ne.delastic(1 - e)
        }, delastic: function (e) {
            var t = 1.5;
            return Math.pow(2, 10 * (e - 1)) * Math.cos(20 * Math.PI * t / 3 * e)
        }
    }, ae = function () {
        function t() {
            var e = arguments.length <= 0 || void 0 === arguments[0] ? "linear" : arguments[0],
                r = arguments.length <= 1 || void 0 === arguments[1] ? 250 : arguments[1],
                i = arguments.length <= 2 || void 0 === arguments[2] ? function () {
                } : arguments[2], n = arguments.length <= 3 || void 0 === arguments[3] ? function () {
                } : arguments[3];
            if (o(this, t), this.duration = r, this.rule = e, this.draw = i, this.end = n, "function" != typeof this.draw)throw new TypeError("Invalid animation draw callback:", i);
            if ("function" != typeof this.end)throw new TypeError("Invalid animation end callback:", n)
        }

        return ie(t, [{
            key: "animate", value: function (t, r) {
                var i = this,
                    o = e.performance && e.performance.now ? e.performance.now() : n("animationStartTime") || Date.now();
                t = t || this.draw, r = r || this.end, this.frame = oe(function (e) {
                    return a(e, t, o, ne[i.rule] || i.rule, i.duration, r, i)
                })
            }
        }, {
            key: "destroy", value: function () {
                if (this.frame) {
                    var e = n("cancelAnimationFrame") || function (e) {
                        };
                    e(this.frame), this.frame = null
                }
                this.draw = null, this.end = null
            }
        }]), t
    }();
    ae.rules = ne;
    var le = function () {
        function t(r, i, n) {
            o(this, t), this.options = r, this.element = i.toLowerCase(), this.type = t.toDashed(n), this.Type = e[n], this.mutationsObserved = !1, this.isObservable = !!e.MutationObserver, t.domReady(this.traverse.bind(this))
        }

        return ie(t, [{
            key: "isValidNode", value: function (e) {
                return !(!e.tagName || e.tagName.toLowerCase() !== this.element || e.getAttribute("data-type") !== this.type)
            }
        }, {
            key: "traverse", value: function () {
                for (var t = document.getElementsByTagName(this.element), r = 0, i = t.length; r < i; r++)this.process(t[r]);
                this.isObservable && !this.mutationsObserved && (new e.MutationObserver(this.observe.bind(this)).observe(document.body, {
                    childList: !0,
                    subtree: !0,
                    attributes: !0,
                    characterData: !0,
                    attributeOldValue: !0,
                    characterDataOldValue: !0
                }), this.mutationsObserved = !0)
            }
        }, {
            key: "observe", value: function (e) {
                for (var t = this, r = 0, i = e.length, o = function () {
                    var i = e[r];
                    if ("attributes" === i.type && "data-type" === i.attributeName && t.isValidNode(i.target) && i.oldValue !== t.type) setTimeout(function () {
                        return t.process(i.target)
                    }); else if (i.addedNodes && i.addedNodes.length)for (var o = 0, n = i.addedNodes.length; o < n; o++)t.process(i.addedNodes[o])
                }; r < i; r++)o()
            }
        }, {
            key: "process", value: function (r) {
                var i = this;
                if (!this.isValidNode(r))return null;
                var o = void 0, n = JSON.parse(JSON.stringify(this.options)), a = null;
                for (o in n)if (n.hasOwnProperty(o)) {
                    var l = t.toAttributeName(o), s = t.parse(r.getAttribute(l));
                    null !== s && void 0 !== s && (n[o] = s)
                }
                return n.renderTo = r, a = new this.Type(n), a.draw && a.draw(), this.isObservable ? (a.observer = new e.MutationObserver(function (e) {
                    e.forEach(function (e) {
                        if ("attributes" === e.type) {
                            var o = e.attributeName.toLowerCase(), n = r.getAttribute(o).toLowerCase();
                            if ("data-type" === o && n && n !== i.type) a.observer.disconnect(), delete a.observer, a.destroy && a.destroy(); else if ("data-" === o.substr(0, 5)) {
                                var l = o.substr(5).split("-").map(function (e, t) {
                                    return t ? e.charAt(0).toUpperCase() + e.substr(1) : e
                                }).join(""), s = {};
                                s[l] = t.parse(r.getAttribute(e.attributeName)), a.update && a.update(s)
                            }
                        }
                    })
                }), a.observer.observe(r, {attributes: !0}), a) : a
            }
        }], [{
            key: "parse", value: function (e) {
                if ("true" === e)return !0;
                if ("false" === e)return !1;
                if ("undefined" !== e) {
                    if ("null" === e)return null;
                    if (/^[-+#.\w\d\s]+(?:,[-+#.\w\d\s]*)+$/.test(e))return e.split(",");
                    try {
                        return JSON.parse(e)
                    } catch (e) {
                    }
                    return e
                }
            }
        }, {
            key: "toDashed", value: function (e) {
                for (var t = e.split(/(?=[A-Z])/), r = 1, i = t.length, o = t[0].toLowerCase(); r < i; r++)o += "-" + t[r].toLowerCase();
                return o
            }
        }, {
            key: "toAttributeName", value: function (e) {
                return "data-" + t.toDashed(e)
            }
        }, {
            key: "domReady", value: function (t) {
                return /comp|inter|loaded/.test((e.document || {}).readyState + "") ? t() : void(e.addEventListener ? e.addEventListener("DOMContentLoaded", t, !1) : e.attachEvent && e.attachEvent("onload", t))
            }
        }]), t
    }(), se = function () {
        function t(e, r, i) {
            o(this, t), t.collection.push(this), this.width = r || 0, this.height = i || 0, this.element = e, this.init()
        }

        return ie(t, [{
            key: "init", value: function () {
                var e = t.pixelRatio;
                this.element.width = this.width * e, this.element.height = this.height * e, this.element.style.width = this.width + "px", this.element.style.height = this.height + "px", this.elementClone = this.element.cloneNode(!0), this.context = this.element.getContext("2d"), this.contextClone = this.elementClone.getContext("2d"), this.drawWidth = this.element.width, this.drawHeight = this.element.height, this.drawX = this.drawWidth / 2, this.drawY = this.drawHeight / 2, this.minSide = this.drawX < this.drawY ? this.drawX : this.drawY, this.elementClone.initialized = !1, this.contextClone.translate(this.drawX, this.drawY), this.contextClone.save(), this.context.translate(this.drawX, this.drawY), this.context.save(), this.context.max = this.contextClone.max = this.minSide, this.context.maxRadius = this.contextClone.maxRadius = null
            }
        }, {
            key: "destroy", value: function () {
                var e = t.collection.indexOf(this);
                ~e && t.collection.splice(e, 1), this.context.clearRect(-this.drawX, -this.drawY, this.drawWidth, this.drawHeight), this.context.max = null, delete this.context.max, this.context.maxRadius = null, delete this.context.maxRadius, this.context = null, this.contextClone = null, this.elementClone = null, this.element = null, this.onRedraw = null
            }
        }, {
            key: "commit", value: function () {
                var e = t.pixelRatio;
                return 1 !== e && (this.contextClone.scale(e, e), this.contextClone.save()), this
            }
        }, {
            key: "redraw", value: function () {
                return this.init(), this.onRedraw && this.onRedraw(), this
            }
        }], [{
            key: "redraw", value: function () {
                for (var e = 0, r = t.collection.length; e < r; e++)t.collection[e].redraw()
            }
        }, {
            key: "pixelRatio", get: function () {
                return e.devicePixelRatio || 1
            }
        }]), t
    }();
    se.collection = [], e.matchMedia && e.matchMedia("screen and (min-resolution: 2dppx)").addListener(se.redraw);
    var de = {
        renderTo: null,
        width: 0,
        height: 0,
        minValue: 0,
        maxValue: 100,
        value: 0,
        units: !1,
        majorTicks: [0, 20, 40, 60, 80, 100],
        minorTicks: 10,
        strokeTicks: !0,
        animatedValue: !1,
        animateOnInit: !1,
        title: !1,
        borders: !0,
        valueInt: 3,
        valueDec: 2,
        majorTicksInt: 1,
        majorTicksDec: 0,
        animation: !0,
        animationDuration: 500,
        animationRule: "cycle",
        colorPlate: "#fff",
        colorPlateEnd: "",
        colorMajorTicks: "#444",
        colorMinorTicks: "#666",
        colorTitle: "#888",
        colorUnits: "#888",
        colorNumbers: "#444",
        colorNeedle: "rgba(240,128,128,1)",
        colorNeedleEnd: "rgba(255,160,122,.9)",
        colorValueText: "#444",
        colorValueTextShadow: "rgba(0,0,0,0.3)",
        colorBorderShadow: "rgba(0,0,0,0.5)",
        colorBorderOuter: "#ddd",
        colorBorderOuterEnd: "#aaa",
        colorBorderMiddle: "#eee",
        colorBorderMiddleEnd: "#f0f0f0",
        colorBorderInner: "#fafafa",
        colorBorderInnerEnd: "#ccc",
        colorValueBoxRect: "#888",
        colorValueBoxRectEnd: "#666",
        colorValueBoxBackground: "#babab2",
        colorValueBoxShadow: "rgba(0,0,0,1)",
        colorNeedleShadowUp: "rgba(2,255,255,0.2)",
        colorNeedleShadowDown: "rgba(188,143,143,0.45)",
        fontNumbers: "Arial",
        fontTitle: "Arial",
        fontUnits: "Arial",
        fontValue: "Arial",
        fontNumbersSize: 20,
        fontTitleSize: 24,
        fontUnitsSize: 22,
        fontValueSize: 40,
        fontNumbersStyle: "normal",
        fontTitleStyle: "normal",
        fontUnitsStyle: "normal",
        fontValueStyle: "normal",
        fontNumbersWeight: "normal",
        fontTitleWeight: "normal",
        fontUnitsWeight: "normal",
        fontValueWeight: "normal",
        needle: !0,
        needleShadow: !0,
        needleType: "arrow",
        needleStart: 5,
        needleEnd: 85,
        needleWidth: 4,
        borderOuterWidth: 3,
        borderMiddleWidth: 3,
        borderInnerWidth: 3,
        borderShadowWidth: 3,
        valueBox: !0,
        valueBoxStroke: 5,
        valueBoxWidth: 0,
        valueText: "",
        valueTextShadow: !0,
        valueBoxBorderRadius: 2.5,
        highlights: [{from: 20, to: 60, color: "#eee"}, {from: 60, to: 80, color: "#ccc"}, {
            from: 80,
            to: 100,
            color: "#999"
        }],
        highlightsWidth: 15
    };
    l.prototype = Object.create(Array.prototype), l.prototype.constructor = l, l.prototype.get = function (e) {
        if ("string" == typeof e)for (var t = 0, r = this.length; t < r; t++) {
            var i = this[t].options.renderTo.tagName ? this[t].options.renderTo : document.getElementById(this[t].options.renderTo || "");
            if (i.getAttribute("id") === e)return this[t]
        } else if ("number" == typeof e)return this[e];
        return null
    };
    var ce = "2.0.8", he = Math.round, ue = Math.abs, fe = new l;
    fe.version = ce;
    var ve = function () {
        function t(r) {
            o(this, t);
            var i = this.constructor.name;
            if ("BaseGauge" === i)throw new TypeError("Attempt to instantiate abstract class!");
            if (fe.push(this), this.version = ce, this.type = e[i], this.initialized = !1, r.minValue = parseFloat(r.minValue), r.maxValue = parseFloat(r.maxValue), r.value = parseFloat(r.value) || 0, r.borders || (r.borderInnerWidth = r.borderMiddleWidth = r.borderOuterWidth = 0), !r.renderTo)throw TypeError("Canvas element was not specified when creating the Gauge object!");
            var n = r.renderTo.tagName ? r.renderTo : document.getElementById(r.renderTo);
            if (!(n instanceof HTMLCanvasElement))throw TypeError("Given gauge canvas element is invalid!");
            r.width = parseFloat(r.width) || 0, r.height = parseFloat(r.height) || 0, r.width && r.height || (r.width || (r.width = n.parentNode ? n.parentNode.offsetWidth : n.offsetWidth), r.height || (r.height = n.parentNode ? n.parentNode.offsetHeight : n.offsetHeight)), this.options = r || {}, this.options.animateOnInit && (this._value = this.options.value, this.options.value = this.options.minValue), this.canvas = new se(n, r.width, r.height), this.canvas.onRedraw = this.draw.bind(this), this.animation = new ae(r.animationRule, r.animationDuration)
        }

        return ie(t, [{
            key: "update", value: function (e) {
                return Object.assign(this.options, e || {}), this.canvas.width = this.options.width, this.canvas.height = this.options.height, this.animation.rule = this.options.animationRule, this.animation.duration = this.options.animationDuration, this.canvas.redraw(), this
            }
        }, {
            key: "destroy", value: function () {
                var e = fe.indexOf(this);
                ~e && fe.splice(e, 1), this.canvas.destroy(), this.canvas = null, this.animation.destroy(), this.animation = null
            }
        }, {
            key: "draw", value: function () {
                this.options.animateOnInit && !this.initialized && (this.value = this._value, this.initialized = !0)
            }
        }, {
            key: "value", set: function (e) {
                var t = this;
                e = parseFloat(e), !isNaN(e) && isFinite(e) || (e = this.options.minValue);
                var r = this.options.value;
                e !== this.options.value && (this.options.animation ? (this._value = e, this.animation.animate(function (i) {
                    t.options.value = r + (e - r) * i, t.draw()
                }, function () {
                    t.options.value = e, delete t._value, t.draw()
                })) : (this.options.value = e, this.draw()))
            }, get: function () {
                return "undefined" == typeof this._value ? this.options.value : this._value
            }
        }], [{
            key: "initialize", value: function (e, t) {
                new le(t, "canvas", e)
            }
        }, {
            key: "version", get: function () {
                return ce
            }
        }]), t
    }();
    "undefined" != typeof e && (e.BaseGauge = ve, (e.document || e).gauges = fe);
    var me = {
        roundRect: c,
        padValue: h,
        formatMajorTickNumber: u,
        radians: f,
        radialPoint: v,
        linearGradient: m,
        drawNeedleShadow: g,
        drawValueBox: p,
        verifyError: s,
        prepareTicks: d,
        drawShadow: b
    }, be = Math.PI, ge = be / 2, pe = Object.assign({}, de, {
        ticksAngle: 270,
        startAngle: 45,
        colorNeedleCircleOuter: "#f0f0f0",
        colorNeedleCircleOuterEnd: "#ccc",
        colorNeedleCircleInner: "#e8e8e8",
        colorNeedleCircleInnerEnd: "#f5f5f5",
        needleCircleSize: 10,
        needleCircleInner: !0,
        needleCircleOuter: !0,
        animationTarget: "needle"
    }), we = function (e) {
        function t(e) {
            return o(this, t), e = Object.assign({}, pe, e || {}), isNaN(e.startAngle) && (e.startAngle = 45), isNaN(e.ticksAngle) && (e.ticksAngle = 270), e.ticksAngle > 360 && (e.ticksAngle = 360), e.ticksAngle < 0 && (e.ticksAngle = 0), e.startAngle < 0 && (e.startAngle = 0), e.startAngle > 360 && (e.startAngle = 360),
                r(this, (t.__proto__ || Object.getPrototypeOf(t)).call(this, e))
        }

        return i(t, e), ie(t, [{
            key: "draw", value: function () {
                try {
                    var e = this.canvas, r = -e.drawX, i = -e.drawY, o = e.drawWidth, n = e.drawHeight,
                        a = this.options;
                    if ("needle" === a.animationTarget) {
                        if (!e.elementClone.initialized) {
                            var l = e.contextClone;
                            l.clearRect(r, i, o, n), l.save(), x(l, a), T(l, a), k(l, a), W(l, a), B(l, a), P(l, a), V(l, a), e.elementClone.initialized = !0
                        }
                        this.canvas.commit(), e.context.clearRect(r, i, o, n), e.context.save(), e.context.drawImage(e.elementClone, r, i, o, n), e.context.save(), N(e.context, a, a.animatedValue ? this.options.value : this.value), A(e.context, a)
                    } else {
                        var s = -me.radians((a.value - a.minValue) / (a.maxValue - a.minValue) * a.ticksAngle);
                        if (e.context.clearRect(r, i, o, n), e.context.save(), x(e.context, a), e.context.rotate(s), T(e.context, a), k(e.context, a), W(e.context, a), B(e.context, a), e.context.rotate(-s), e.context.save(), !e.elementClone.initialized) {
                            var d = e.contextClone;
                            d.clearRect(r, i, o, n), d.save(), P(d, a), V(d, a), A(d, a), e.elementClone.initialized = !0
                        }
                        e.context.drawImage(e.elementClone, r, i, o, n)
                    }
                    N(e.context, a, a.animatedValue ? this.options.value : this.value), re(t.prototype.__proto__ || Object.getPrototypeOf(t.prototype), "draw", this).call(this)
                } catch (e) {
                    me.verifyError(e)
                }
                return this
            }
        }]), t
    }(ve);
    "undefined" != typeof e && (e.RadialGauge = we), ve.initialize("RadialGauge", pe);
    var ye = Object.assign({}, de, {
        borderRadius: 0,
        barBeginCircle: 30,
        barWidth: 20,
        barStrokeWidth: 0,
        barProgress: !0,
        colorBarStroke: "#222",
        colorBar: "#ccc",
        colorBarEnd: "",
        colorBarProgress: "#888",
        colorBarProgressEnd: "",
        needleWidth: 6,
        tickSide: "both",
        needleSide: "both",
        numberSide: "both",
        ticksWidth: 10,
        ticksWidthMinor: 5,
        ticksPadding: 5,
        barLength: 85,
        fontTitleSize: 26,
        highlightsWidth: 10
    }), xe = function (e) {
        function n(e) {
            return o(this, n), e = Object.assign({}, ye, e || {}), e.barStrokeWidth >= e.barWidth && (e.barStrokeWidth = he(e.barWidth / 2)), e.hasLeft = z("right", e), e.hasRight = z("left", e), r(this, (n.__proto__ || Object.getPrototypeOf(n)).call(this, e))
        }

        return i(n, e), ie(n, [{
            key: "draw", value: function () {
                try {
                    var e = this.canvas, r = -e.drawX, i = -e.drawY, o = e.drawWidth, a = e.drawHeight,
                        l = this.options;
                    if (!e.elementClone.initialized) {
                        var s = e.contextClone;
                        s.clearRect(r, i, o, a), s.save(), this.drawBox = E(s, l, r, i, o, a), D.apply(void 0, [s, l].concat(t(this.drawBox))), e.context.barDimensions = s.barDimensions, _(s, l), F(s, l), Y(s, l), q(s, l), H(s, l), J(s, l), e.elementClone.initialized = !0
                    }
                    this.canvas.commit(), e.context.clearRect(r, i, o, a), e.context.save(), e.context.drawImage(e.elementClone, r, i, o, a), e.context.save(), L.apply(void 0, [e.context, l].concat(t(this.drawBox))), Z(e.context, l), ee.apply(void 0, [e.context, l, l.animatedValue ? this.options.value : this.value].concat(t(this.drawBox))), re(n.prototype.__proto__ || Object.getPrototypeOf(n.prototype), "draw", this).call(this)
                } catch (e) {
                    me.verifyError(e)
                }
                return this
            }
        }]), n
    }(ve);
    "undefined" != typeof e && (e.LinearGauge = xe), ve.initialize("LinearGauge", ye), "undefined" != typeof module && Object.assign(e, {
        Collection: l,
        GenericOptions: de,
        Animation: ae,
        BaseGauge: ve,
        drawings: me,
        SmartCanvas: se,
        vendorize: n
    })
}("undefined" != typeof module ? module.exports : window);
//# sourceMappingURL=gauge.min.js.map
