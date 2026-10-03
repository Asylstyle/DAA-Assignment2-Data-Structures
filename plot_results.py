import csv
from pathlib import Path

import matplotlib
matplotlib.use("Agg")

import matplotlib.pyplot as plt


def main():
    with open("results/results.csv", newline="", encoding="utf-8") as file:
        rows = list(csv.DictReader(file))

    if len(rows) != 36:
        raise ValueError("Expected 36 benchmark rows")

    output = Path("results/plots")
    output.mkdir(parents=True, exist_ok=True)

    titles = {
        "W1": "Random access: 10,000 queries",
        "W2": "Search: 1,000 queries, 50% present",
        "W3": "Insert and remove: 1,000 of each",
        "W4": "Priority processing: n inserts and n extractions",
    }

    metrics = ["time_ms", "steps", "moves", "comparisons"]
    labels = ["Median time (ms)", "Steps", "Moves", "Comparisons"]

    colors = {
        ("DynamicArray", "-"): "#2563eb",
        ("MyLinkedList", "-"): "#ea580c",
        ("DynamicArray", "head"): "#2563eb",
        ("DynamicArray", "middle"): "#2563eb",
        ("MyLinkedList", "head"): "#ea580c",
        ("MyLinkedList", "middle"): "#ea580c",
        ("MinHeap", "-"): "#16a34a",
    }

    for workload in ["W1", "W2", "W3", "W4"]:
        selected = []

        for row in rows:
            if row["workload"] == workload:
                selected.append(row)

        series_keys = sorted({
            (row["structure"], row["variant"])
            for row in selected
        })

        figure, axes = plt.subplots(2, 2, figsize=(12, 8))

        for panel in range(4):
            axis = axes.flat[panel]
            metric = metrics[panel]

            for structure, variant in series_keys:
                series = []

                for row in selected:
                    if (row["structure"] == structure
                            and row["variant"] == variant):
                        series.append(row)

                series.sort(key=lambda row: int(row["n"]))

                sizes = [int(row["n"]) for row in series]
                values = [float(row[metric]) for row in series]

                legend = structure
                if variant != "-":
                    legend += " (" + variant + ")"

                style = "--" if variant == "middle" else "-"
                marker = "s" if variant == "middle" else "o"

                axis.plot(
                    sizes,
                    values,
                    label=legend,
                    color=colors[(structure, variant)],
                    linestyle=style,
                    marker=marker,
                    linewidth=2,
                    markersize=6,
                )

            axis.set_xscale("log")
            axis.set_xticks([100, 1000, 10000, 100000])
            axis.set_xticklabels(["100", "1,000", "10,000", "100,000"])

            # A linear region near zero keeps zero counters visible.
            axis.set_yscale("symlog", linthresh=1)
            axis.set_xlabel("n (elements)")
            axis.set_ylabel(labels[panel])
            axis.set_title(labels[panel])
            axis.grid(True, which="both", alpha=0.25)
            axis.legend(fontsize=8)

        figure.suptitle(
            workload + " - " + titles[workload],
            fontsize=14,
        )
        figure.text(
            0.5, 0.015,
            "x: logarithmic; y: symmetric logarithmic "
            "(linear between -1 and 1). "
            "Time: median of 5 runs after 2 warm-ups.",
            ha="center",
            fontsize=8,
        )
        figure.tight_layout(rect=[0, 0.045, 1, 0.95])

        destination = output / (workload + ".png")
        figure.savefig(destination, dpi=180)
        plt.close(figure)

        print("Saved", destination)


if __name__ == "__main__":
    main()
