#!/usr/bin/env python3
"""Rebuild assignment figures from the benchmark CSV (no Java execution)."""
import argparse
import csv
import math
from pathlib import Path

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.ticker import FixedLocator, FuncFormatter, NullLocator

ROOT = Path(__file__).resolve().parents[1]
SIZES = [1000, 3162, 10000, 31623, 100000]
ALGORITHMS = ['Kruskal', 'Prim', 'Kosaraju', 'Kahn', 'DagShortestPath']
PREFIX = dict(zip(ALGORITHMS, ['undirected', 'undirected', 'directed', 'dag', 'dag']))
COLORS = dict(zip(ALGORITHMS, ['#2463A0', '#CA7518', '#747C29', '#AC477D', '#594A92']))
MARKERS = dict(zip(ALGORITHMS, ['o', 's', '^', 'D', 'v']))
STYLES = dict(zip(ALGORITHMS, ['-', '--', '-.', ':', '--']))


def read_data(path):
    with path.open(newline='', encoding='utf-8') as stream:
        reader = csv.DictReader(stream)
        expected = 'algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result'.split(',')
        if reader.fieldnames != expected:
            raise ValueError('Unexpected CSV header')
        rows = []
        for record in reader:
            row = {key: (value if key in ('algorithm', 'graph') else
                         float(value) if key == 'time_ms' else int(value))
                   for key, value in record.items()}
            if not math.isfinite(row['time_ms']) or row['time_ms'] <= 0:
                raise ValueError('Log plots require positive finite times')
            rows.append(row)
    expected_cases = {(a, f'{PREFIX[a]}_{d}', n, n * factor)
                      for a in ALGORITHMS for d, factor in [('sparse', 4), ('dense', 20)] for n in SIZES}
    cases = {(r['algorithm'], r['graph'], r['n'], r['m']) for r in rows}
    if len(rows) != 50 or cases != expected_cases:
        raise ValueError('Expected exactly 50 distinct benchmark cases')
    for r in rows:
        factor = 2 if r['algorithm'] in ('Prim', 'Kosaraju') else 1
        if r['edge_scans'] != factor * r['m']:
            raise ValueError('Unexpected edge-scan count; revise chart grouping')
    return rows


def select(rows, algorithm, density):
    return sorted((r for r in rows if r['algorithm'] == algorithm
                   and r['graph'] == f'{PREFIX[algorithm]}_{density}'), key=lambda r: r['n'])


def axes_style(ax, ylabel):
    ax.set_xscale('log')
    ax.set_yscale('log')
    ax.xaxis.set_major_locator(FixedLocator(SIZES))
    ax.xaxis.set_major_formatter(FuncFormatter(lambda x, _: f'{int(x):,}'))
    ax.xaxis.set_minor_locator(NullLocator())
    ax.set_xlabel('Vertices, n (log scale)')
    ax.set_ylabel(ylabel + ' (log scale)')
    ax.grid(axis='both', which='major', color='#DDE1E5', linewidth=0.7)
    ax.set_axisbelow(True)
    ax.spines[['top', 'right']].set_visible(False)


def line(ax, rows, a, density, metric, label=None):
    points = select(rows, a, density)
    ax.plot([r['n'] for r in points], [r[metric] for r in points],
            color=COLORS[a], marker=MARKERS[a], linestyle=STYLES[a],
            label=label or a, linewidth=1.9, markersize=5)


def save(fig, output, name, title, note):
    fig.suptitle(title, fontsize=17, fontweight='bold', y=0.98)
    fig.text(0.07, 0.035, note + '\nSource: results/results.csv | DAA-AS-3', fontsize=9, color='#4C545D')
    fig.subplots_adjust(left=0.08, right=0.98, bottom=0.23, top=0.81, wspace=0.25)
    for extension in ('png', 'pdf'):
        fig.savefig(output / f'{name}.{extension}', dpi=220, facecolor='white')
    plt.close(fig)


def paired(rows, output, name, title, algorithms, metric, ylabel, note, labels=None):
    fig, axes = plt.subplots(1, 2, figsize=(12.5, 5.6), sharey=True)
    for ax, density, factor in zip(axes, ['sparse', 'dense'], [4, 20]):
        axes_style(ax, ylabel)
        ax.set_title(f'{density.capitalize()}: m = {factor}n', pad=12)
        for a in algorithms:
            line(ax, rows, a, density, metric, (labels or {}).get(a))
        ax.legend(frameon=False, fontsize=9, loc='upper left')
    save(fig, output, name, title, note)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--csv', type=Path, default=ROOT / 'results/results.csv')
    parser.add_argument('--out', type=Path, default=ROOT / 'results/plots')
    args = parser.parse_args()
    rows = read_data(args.csv)
    args.out.mkdir(parents=True, exist_ok=True)
    plt.rcParams.update({'font.family': 'DejaVu Sans', 'font.size': 10,
                         'text.color': '#222B34', 'axes.labelcolor': '#222B34', 'pdf.fonttype': 42})
    timing_note = 'Median of 5 measured runs after 2 warm-ups per case; raw run times and uncertainty intervals are unavailable.'
    paired(rows, args.out, '01_mst_time', 'MST runtime: Kruskal and Prim', ALGORITHMS[:2], 'time_ms', 'Time, ms', timing_note)
    paired(rows, args.out, '02_directed_time', 'Directed-graph algorithm runtime', ALGORITHMS[2:], 'time_ms', 'Time, ms',
           timing_note + '\nKosaraju solves SCC; Kahn and DagShortestPath solve different DAG tasks. This is not a ranking of substitutes.')
    paired(rows, args.out, '03_edge_scans', 'Instrumented edge scans', ['Kruskal', 'Prim'], 'edge_scans', 'Edge scans',
           'Coincident series are grouped after checking every CSV row. Counters exclude some setup passes; they are not total work.',
           {'Kruskal': 'Kruskal / Kahn / DagShortestPath = m', 'Prim': 'Prim / Kosaraju = 2m'})
    paired(rows, args.out, '04_mst_comparisons', 'MST comparator calls', ALGORITHMS[:2], 'comparisons', 'Comparator calls',
           'Kruskal counts sorting comparisons; Prim counts priority-queue comparisons. These are different operations.')
    fig, axes = plt.subplots(1, 2, figsize=(12.5, 5.6))
    for ax, a, metric, label in zip(axes, ['Kruskal', 'Kosaraju'], ['dsu_ops', 'max_depth'],
                                   ['DSU operations', 'Maximum explicit stack size']):
        axes_style(ax, label)
        ax.set_title(a, pad=12)
        for density, color, marker, style in [('sparse', '#2463A0', 'o', '-'), ('dense', '#CA7518', 's', '--')]:
            points = select(rows, a, density)
            ax.plot([r['n'] for r in points], [r[metric] for r in points], color=color, marker=marker,
                    linestyle=style, label=f'{density}: m = {4 if density == "sparse" else 20}n')
        ax.legend(frameon=False, fontsize=9)
    save(fig, args.out, '05_dsu_and_stack', 'DSU operations and explicit stack size',
         'Independent y scales. DSU operations = find calls + successful unions. Stack size is not recursive call depth.')
    print(f'Validated {len(rows)} rows; wrote 5 PNG and 5 PDF figures to {args.out.resolve()}')


if __name__ == '__main__':
    main()
